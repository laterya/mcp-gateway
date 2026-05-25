# MCP Gateway

代理多个 MCP Server，为 AI 客户端提供统一接入点的网关服务。

## Language

### 传输与会话

**Transport（传输）**:
客户端与网关之间的通信方式。当前支持 SSE 和 Streamable HTTP 两种。
_Avoid_: 连接方式、协议（协议指 MCP 协议本身）

**Session（会话）**:
一个已鉴权的逻辑会话，由 Session ID 标识，不绑定特定传输方式。SSE 传输的会话附带一个 Sink 用于推送；Streamable HTTP 传输的会话无 Sink。SSE 在 GET 连接时创建会话；Streamable HTTP 在 chain 的 Session 节点中于 initialize 请求时创建会话。
_Avoid_: 连接（连接是传输层的概念）

**Gateway（网关）**:
一组 MCP 工具的配置集合，拥有独立的鉴权配置和工具列表。客户端通过 Gateway ID 选择接入哪个网关。
_Avoid_: 服务、服务实例

**Tool（工具）**:
网关暴露给 MCP 客户端的一个可调用能力，绑定一个 HTTP 协议配置和字段映射。

**Protocol Mapping（协议映射）**:
MCP 工具参数到后端 HTTP API 字段的映射规则，定义请求和响应的字段转换关系。

## Flagged ambiguities

- "Session" 在代码中既指 SSE 长连接又指逻辑会话。决策：统一为逻辑会话，传输细节通过 SessionConfigVO 的字段区分（sink 是否为空）。
