# MCP Gateway

基于 DDD 六边形架构的 MCP（Model Context Protocol）网关服务。代理多个 MCP Server，为 AI 客户端提供统一接入点。

## Tech Stack

- JDK 21 + Spring Boot 3.5 + Spring AI 1.1.2
- Maven 多模块 + Lombok + Project Reactor
- MyBatis 3.0 + MySQL 8.0（DAO 层）
- Guava 33.4（RateLimiter 令牌桶）+ Apache Commons Lang3（API Key 生成）+ Jackson（JSON 解析）
- SSE（Server-Sent Events）作为 MCP 传输协议
- Spring AI 1.1.2（OpenAiChatModel + MCP Client SSE Transport，domain/llm 限界上下文）

## Commands

```bash
mvn clean install -DskipTests                                     # 全量构建
docker compose -f docs/dev-ops/docker-compose-environment.yml up -d  # 启动 MySQL
mvn spring-boot:run -pl mcp-gateway-app                           # 启动服务（http://localhost:8090/api-gateway）
mvn test -pl mcp-gateway-app                                      # 运行所有测试（需 Docker MySQL）
mvn test -pl mcp-gateway-app -Dtest="cn.laterya.ai.dao.**"        # 仅 DAO 测试
mvn test -pl mcp-gateway-app -Dtest="cn.laterya.ai.domain.auth.**" # 仅鉴权测试
mvn test -pl mcp-gateway-app -Dtest="cn.laterya.ai.domain.protocol.**" # 仅协议测试
mvn test -pl mcp-gateway-app -Dtest="cn.laterya.ai.domain.gateway.**" # 仅网关配置测试
```

**ApiTest 运行前提：** 先 `mvn spring-boot:run -pl mcp-gateway-app` 启动服务，再运行测试。测试使用 `gateway_001`（数据库种子数据中的网关 ID）。

**环境要求：** Docker MySQL 运行在 13306 端口，数据库 `mcp_gateway`。API 集成测试需设置 `OPENAI_API_KEY` 和 `OPENAI_BASE_URL` 环境变量。

**管理后台：**
```bash
cd ../mcp-gateway-demo-server && mvn spring-boot:run     # 启动 Swagger 测试服务（8701 端口）
# 管理后台已内嵌在网关静态资源中，启动网关后直接访问：
# http://localhost:8090/api-gateway/index.html（登录 admin/password123）
```

**demo-server** 是独立测试项目（`../mcp-gateway-demo-server`），带 Swagger 注解的 Web 接口，用于验证 OpenAPI JSON 解析。启动后访问 `http://localhost:8701/v3/api-docs` 获取 JSON。

## Architecture — DDD Hexagonal (7 Modules)

```
trigger ──→ case ──→ domain
   │          │        │
   │          └── types ┘
   │
infrastructure (实现 domain 的 Port 接口)
api       (对外 DTO / Facade 接口)
app       (Spring Boot 启动模块，装配层)
```

| Module | Layer | Responsibility |
|--------|-------|---------------|
| `types` | 基础类型 | 通用响应、常量、异常、工具类 |
| `domain` | 领域层 | 实体、值对象、领域服务、Port 接口。含 6 个限界上下文：`session` / `auth` / `protocol` / `gateway` / `admin` / `llm` |
| `case` | 用例层 | 编排领域服务，事务边界（依赖 spring-tx、spring-web）。含 mcp 编排（session/message 两条责任链）和 admin 编排（CRUD 转发） |
| `infrastructure` | 基础设施层 | 实现 domain 的 Port，对接外部系统 |
| `api` | 接口定义 | 对外 Facade 接口 + DTO |
| `trigger` | 触发器层 | HTTP Controller，调用 case/api |
| `app` | 启动模块 | Application.java + application.yml |

**依赖规则（重要）：**
- domain 原则上只依赖 types，已知例外：reactor-core（响应式流）、Spring AI（LLM 限界上下文）
- infrastructure 实现 domain 定义的 Port 接口（依赖倒置，接口在 `domain/*/adapter/repository/`，实现在 `infrastructure/adapter/repository/`）
- trigger → case → domain，不允许反向依赖
- 所有模块共享 `cn.laterya.ai` 基础包名

**Auth 限界上下文（domain/auth/）：** 注册（gw- 前缀 API Key）→ 鉴权（4 步链）→ 限流（Guava RateLimiter 令牌桶，按 gatewayId+apiKey 维度）
- `AuthLicenseService` 鉴权链：鉴权模式 → 查配置 → 检查启用 → 检查过期（null = 永久有效）
- `AuthRateLimitService`：每小时配额换算为每秒速率；IllegalStateException（无配置）→ 放行，IllegalArgumentException（配置无效）→ 限流

**Protocol 限界上下文（domain/protocol/）：** Swagger OpenAPI JSON → `HTTPProtocolVO` + `ProtocolMapping` → 落库
- 策略模式：`AnalysisTypeEnum` 枚举路由，RequestBody 和 Parameters 策略可共存
- `IProtocolAnalysis`（解析）→ `IProtocolStorage`（落库），`import_gateway_protocol` 端点串联全链路

**Gateway 限界上下文（domain/gateway/）：** 网关和工具配置 CRUD，充血工厂方法，`saveGatewayConfig` upsert（存在则 update 否则 insert）

**Admin 跨域编排（domain/admin/ + case/admin/ + trigger/AdminController）：** 20+ 端点（/admin/），含 CRUD + import/analysis + LLM 测试
- case/admin：`AdminGatewayService`/`AdminAuthService`/`AdminProtocolService`/`AdminManageService`/`AdminLLMService` 编排转发
- API 接口在 `mcp-gateway-api` 模块 `IAdminService` 统一定义

**LLM 限界上下文（domain/llm/）：** 内嵌 LLM 能力，动态构建 MCP 客户端连接回自身网关 SSE 端点
- `LLMService`：ConcurrentHashMap<sseEndpoint, ChatClient> 缓存，`HttpClientSseClientTransport` → `McpSyncClient` → `SyncMcpToolCallbackProvider`
- `AdminLLMService`（case 层）：拼接 SSE 端点 URL（`contextPath + /gatewayId/mcp/sse?api_key=xxx`），委托 `ILLMService`
- `POST /admin/test_call_gateway`：管理员发消息 → LLM 通过网关调 MCP 工具 → 返回结果，走完全链路

## 表结构关系

```
mcp_gateway ──1:1── mcp_gateway_auth        (网关鉴权，按 gateway_id 关联)
       │
       └──1:N── mcp_gateway_tool             (网关下的工具，按 gateway_id 关联)
                      │
                      └──N:1── mcp_protocol_http     (HTTP 协议配置，按 protocol_id 关联)
                      │
                      └──1:N── mcp_protocol_mapping  (请求/响应字段映射，按 protocol_id 关联)
```

拆分思路：工具描述（`mcp_gateway_tool`）与协议细节（`mcp_protocol_http`）解耦，一个工具绑定一种协议，通过 `protocol_mapping` 定义 MCP 字段到 HTTP 字段的映射关系。

## MCP SSE Protocol Flow

```
Client GET /{gatewayId}/mcp/sse
  → createSession() → Sink 推送 endpoint 事件
  → 返回 SSE 流（sink.asFlux()）
Client POST /{gatewayId}/mcp/sse?sessionId=xxx
  → 服务端处理 → 通过同一 Sink 推送响应
会话超时(30min) / 断开 → removeSession() 清理
```

## Code Conventions

- DI 使用 `@Resource`（非 `@Autowired`）；Handler Bean 用 `@Service("beanName")` 命名注册
- PO 统一使用 `@Data @Builder @NoArgsConstructor @AllArgsConstructor`
- 领域模型用充血模式：行为内聚到 Entity/VO，避免逻辑散落在 Service
- VO vs Entity：不落库、无持久化标识的是 VO（如 SessionConfigVO）
- 并发安全：ConcurrentHashMap（容器安全）+ volatile 字段（元素可见性），两者缺一不可
- 注释语言：中文，解释 WHY 不解释 WHAT
- Port 接口（如 ISessionPort、ISessionRepository）在 domain/adapter 下定义，infrastructure/adapter 下实现

## Gotchas

- **MyBatis Mapper 位置**：DAO 接口和 PO 在 `infrastructure` 模块，但 Mapper XML 和 MyBatis 配置在 `app` 模块的 `resources/mybatis/`
- **数据库初始化**：SQL 文件名为 `ai_mcp_gateway.sql` 但实际建库名为 `mcp_gateway`，Docker 自动加载 `docker-entrypoint-initdb.d`
- **会话编排责任链**：`RootNode → VerifyNode(AuthLicenseService鉴权) → CreateSessionNode → SseResponseNode`，在 case 层组装
- **消息编排责任链**：`MessageRootNode(AuthRateLimitService限流) → MessageSessionNode → MessageHandlerNode`，在 case 层组装
- **HTTP 客户端配置位置**：`GenericHttpGateway`（Retrofit2 接口）和 `HTTPClientConfig`（OkHttp 连接池）都在 infrastructure 模块
- **消息路由使用策略模式**：`SessionMessageService` 通过 `Map<String, IRequestHandler>` 自动注入，按 method 字段分发
- **JSON-RPC 消息类型**：`McpSchemaVO` 使用 sealed interface + record（JDK 21 特性）
- **api_key 传播机制**：`createSession()` 将 api_key 拼入 endpoint 事件的 URL，客户端 POST 回调时自动携带，这是限流能工作的关键
- **鉴权与限流分离**：鉴权在 SSE 连接时（VerifyNode 调 AuthLicenseService），限流在消息处理时（MessageRootNode 仅对 TOOLS_CALL 调 AuthRateLimitService）
- **AuthRateLimitService 异常策略**：IllegalStateException（无配置）→ 放行，IllegalArgumentException（配置无效）→ 限流
- **Gateway save upsert**：`saveGatewayConfig` 先查 `queryByGatewayId`，存在则 `updateById`，不存在则 `insert`——编辑弹窗复用 save 端点
- **case 层不依赖 infrastructure**：case 只调 domain Port 接口（如 `IAuthRepository`），不能直接注入 DAO。之前违反此规则（AdminAuthService 直接注 DAO）已修正
- **Maven scope 覆盖**：app 模块直接声明某依赖为 test scope 会覆盖 domain 传来的 compile scope。若 domain 已有 compile 声明，app 不需要再声明
- **OpenAI tool schema 要求**：protocol_mapping 的 request 映射必须有根 object 节点包裹（如 `GetEmployeeDetailRequest`），不能直接是裸字段，否则 OpenAI 拒绝
- **SSE 端点 URL 拼接**：`server.servlet.context-path` 已含前导 `/`（如 `/api-gateway`），拼接路径时不要再额外加 `/`
- **LLM 默认模型**：application.yml 中 `OPENAI_MODEL` 默认值为 `gpt-5.2`
- **Docker MySQL 连接**：`docker exec mcp-gateway-mysql mysql -u root -p123456 mcp_gateway`，密码 `123456`

## Agent Skills

本项目已安装 [Matt Pocock Skills](https://github.com/mattpocock/skills.git)（14 个 skill），通过 `skills` CLI 全局管理。

### 可用 Skills

| Skill | 用途 |
|-------|------|
| `diagnose` | 系统性调试循环：复现 → 最小化 → 假设 → 打点 → 修复 → 回归测试 |
| `tdd` | 测试驱动开发：红-绿-重构循环 |
| `triage` | Issue 分拣状态机 |
| `to-issues` | 将计划/PRD 拆解为独立可抓取的 issue |
| `to-prd` | 将对话上下文转为 PRD 并发布到 issue tracker |
| `brainstorming` | 实现前的需求探索与设计对齐 |
| `grill-me` | 对设计进行追问直到达成共识 |
| `grill-with-docs` | 对照领域模型挑战计划，更新 CONTEXT.md 和 ADR |
| `prototype` | 构建可丢弃的原型以验证设计 |
| `improve-codebase-architecture` | 寻找代码库深化机会 |
| `handoff` | 将当前对话压缩为交接文档 |
| `write-a-skill` | 创建新的 agent skill |
| `caveman` | 超压缩通信模式（节省约 75% token） |
| `zoom-out` | 从高层视角审视项目 |

### 使用方式

在对话中直接提及 skill 名称或触发短语即可自动调用。例如：
- "debug this bug" → 触发 `diagnose`
- "add tests for X" → 触发 `tdd`
- "triage this issue" → 触发 `triage`
- "break this plan into issues" → 触发 `to-issues`

### Issue Tracker 配置

- **平台**：GitHub，仓库 `laterya/mcp-gateway`，使用 `gh` CLI
- **Triage 标签**：`needs-triage` / `needs-info` / `ready-for-agent` / `ready-for-human` / `wontfix`
- 详见 `docs/agents/issue-tracker.md`、`docs/agents/triage-labels.md`
- 领域文档布局：`CONTEXT.md` + `docs/adr/`，详见 `docs/agents/domain.md`
