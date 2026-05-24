# MCP Gateway

基于 DDD 六边形架构的 MCP（Model Context Protocol）网关服务。代理多个 MCP Server，为 AI 客户端提供统一接入点。

## Tech Stack

- JDK 21 + Spring Boot 3.5 + Spring AI 1.1.2
- Maven 多模块 + Lombok + Project Reactor
- MyBatis 3.0 + MySQL 8.0（DAO 层）
- Guava 33.4（RateLimiter 令牌桶）+ Apache Commons Lang3（API Key 生成）
- SSE（Server-Sent Events）作为 MCP 传输协议
- Spring AI 仅在 test scope（MCP Client 测试用）

## Commands

```bash
mvn clean install -DskipTests                                     # 全量构建
docker compose -f docs/dev-ops/docker-compose-environment.yml up -d  # 启动 MySQL
mvn spring-boot:run -pl mcp-gateway-app                           # 启动服务（http://localhost:8090/api-gateway）
mvn test -pl mcp-gateway-app                                      # 运行所有测试（需 Docker MySQL）
mvn test -pl mcp-gateway-app -Dtest="cn.laterya.ai.dao.**"        # 仅 DAO 测试
mvn test -pl mcp-gateway-app -Dtest="cn.laterya.ai.domain.auth.**" # 仅鉴权测试
```

**ApiTest 运行前提：** 先 `mvn spring-boot:run -pl mcp-gateway-app` 启动服务，再运行测试。测试使用 `gateway_001`（数据库种子数据中的网关 ID）。

**环境要求：** Docker MySQL 运行在 13306 端口，数据库 `mcp_gateway`。API 集成测试需设置 `OPENAI_API_KEY` 和 `OPENAI_BASE_URL` 环境变量。

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
| `domain` | 领域层 | 实体、值对象、领域服务、Port 接口。含 `session` 和 `auth` 两个限界上下文 |
| `case` | 用例层 | 编排领域服务，事务边界（依赖 spring-tx、spring-web）。含两条责任链：session 编排（建立 SSE 连接）和 message 编排（消息处理） |
| `infrastructure` | 基础设施层 | 实现 domain 的 Port，对接外部系统 |
| `api` | 接口定义 | 对外 Facade 接口 + DTO |
| `trigger` | 触发器层 | HTTP Controller，调用 case/api |
| `app` | 启动模块 | Application.java + application.yml |

**依赖规则（重要）：**
- domain 只依赖 types，不依赖任何框架（唯一例外：reactor-core）
- infrastructure 实现 domain 定义的 Port 接口（依赖倒置）
- trigger → case → domain，不允许反向依赖
- 所有模块共享 `cn.laterya.ai` 基础包名

**依赖倒置路径（adapter）：**
```
domain/session/adapter/repository/ISessionRepository.java   ← 接口定义
domain/auth/adapter/repository/IAuthRepository.java         ← 接口定义
infrastructure/adapter/repository/SessionRepository.java    ← 实现（注入 DAO）
infrastructure/adapter/repository/AuthRepository.java       ← 实现（注入 DAO）
```

**Auth 限界上下文（domain/auth/）：**
- `AuthRegisterService`：生成 `gw-` 前缀的 48 位 API Key，落库
- `AuthLicenseService`：4 步鉴权链 — 网关鉴权模式 → 查询配置 → 检查启用状态 → 检查过期时间（null = 永久有效）
- `AuthRateLimitService`：Guava Cache + RateLimiter 令牌桶，按 `gatewayId+apiKey` 维度限流，每小时配额换算为每秒速率
- `AuthStatusEnum`：嵌套枚举 — `GatewayConfig`(NOT_VERIFIED/STRONG_VERIFIED) + `AuthConfig`(DISABLE/ENABLE)

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
