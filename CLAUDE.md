# MCP Gateway

基于 DDD 六边形架构的 MCP（Model Context Protocol）网关服务。代理多个 MCP Server，为 AI 客户端提供统一接入点。

## Tech Stack

- JDK 21 + Spring Boot 3.5 + Spring AI 1.1.2
- Maven 多模块 + Lombok + Project Reactor
- SSE（Server-Sent Events）作为 MCP 传输协议

## Commands

```bash
mvn clean install                        # 全量构建（跳过测试 -DskipTests）
mvn spring-boot:run -pl mcp-gateway-app  # 启动服务（端口 8090）
mvn test -pl mcp-gateway-domain          # 单模块测试
```

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
| `domain` | 领域层 | 实体、值对象、领域服务、Port 接口 |
| `case` | 用例层 | 编排领域服务，事务边界（依赖 spring-tx） |
| `infrastructure` | 基础设施层 | 实现 domain 的 Port，对接外部系统 |
| `api` | 接口定义 | 对外 Facade 接口 + DTO |
| `trigger` | 触发器层 | HTTP Controller，调用 case/api |
| `app` | 启动模块 | Application.java + application.yml |

**依赖规则（重要）：**
- domain 只依赖 types，不依赖任何框架（唯一例外：reactor-core）
- infrastructure 实现 domain 定义的 Port 接口（依赖倒置）
- trigger → case → domain，不允许反向依赖
- 所有模块共享 `cn.laterya.ai` 基础包名

## MCP SSE Protocol Flow

```
Client GET /{gatewayId}/mcp/sse
  → createSession() → Sink 推送 endpoint 事件
  → 返回 SSE 流（sink.asFlux()）
Client POST /{gatewayId}/mcp/message?sessionId=xxx
  → 服务端处理 → 通过同一 Sink 推送响应
会话超时(30min) / 断开 → removeSession() 清理
```

## Code Conventions

- 领域模型用充血模式：行为内聚到 Entity/VO，避免逻辑散落在 Service
- VO vs Entity：不落库、无持久化标识的是 VO（如 SessionConfigVO）
- 并发安全：ConcurrentHashMap（容器安全）+ volatile 字段（元素可见性），两者缺一不可
- 注释语言：中文，解释 WHY 不解释 WHAT
- Port 接口（如 ISessionManagementService）放在 domain，实现在 domain 或 infrastructure
