# 统一 Session 模型

SSE 和 Streamable HTTP 两种传输共享同一个 `SessionConfigVO` 实体和 `SessionManagementService` 会话管理。SSE 传输的 Session 带 `Sinks.Many` sink 字段用于推送响应；Streamable HTTP 传输的 Session sink 为 null，响应直接写在 HTTP POST response body 中。通过 sink 是否为空区分传输类型。

## Considered Options

- **独立 Session 模型**：每种传输各一套 Session 实体和管理逻辑，完全隔离
- **统一模型（选定）**：共享 SessionConfigVO，sink 可空，共享 ConcurrentHashMap session store

选择统一的理由：Session 的本质是"一个已鉴权的 MCP 逻辑会话"，传输方式只是细节差异。共享 session store 可复用鉴权、限流、超时清理等逻辑，避免重复实现。

## Consequences

- `SessionConfigVO` 需要处理 sink 为 null 的分支（SseResponseNode 等节点需要判空）
- 未来新增传输方式时只需扩展 SessionConfigVO 字段，不需要新建一套 session 管理
