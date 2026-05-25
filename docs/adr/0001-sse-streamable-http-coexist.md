# SSE 与 Streamable HTTP 共存

MCP 规范已在 2025-03-26 版本将 SSE 传输标记为废弃，推荐 Streamable HTTP。但当前已有客户端使用 SSE 传输连接网关，且 SSE 传输在长连接场景下仍有性能优势。决策：两种传输共存于同一网关，各自独立端点（`/mcp/sse` 和 `/mcp`），共享 case 层业务逻辑。

## Considered Options

- **替换 SSE**：破坏现有客户端，风险高
- **共存（选定）**：SSE 端点不动，新增 Streamable HTTP 端点，共享鉴权/限流/处理逻辑
- **仅 Streamable HTTP**：同替换，无法向后兼容

## Consequences

- 维护两套 Controller 和两条责任链，但共享链节点（鉴权、限流、Handler）
- 未来如果需要废弃 SSE，只需移除 SSE Controller 和对应链组装，共享节点不受影响
