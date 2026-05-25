# MCP Gateway — Domain Context

## Ubiquitous Language

| Term | Definition |
|------|-----------|
| **Gateway** | A registered MCP gateway instance that proxies requests to downstream MCP tools |
| **Tool** | An MCP tool exposed by a gateway, backed by an HTTP/SSE protocol mapping |
| **Protocol** | The HTTP protocol configuration (Swagger/OpenAPI-derived) for a tool |
| **Protocol Mapping** | Field-level mapping from MCP tool parameters to HTTP request/response fields |
| **API Key** | A `gw-` prefixed authentication key registered per gateway |
| **Session** | An SSE connection from a client to the gateway, identified by sessionId |
| **Rate Limit** | Per-gateway, per-API-key token-bucket rate limiting via Guava RateLimiter |
| **Auth License** | The authentication and authorization configuration for a gateway |

## Bounded Contexts

| Context | Module | Responsibility |
|---------|--------|---------------|
| **Session** | `domain/session` | SSE session lifecycle (create, maintain, cleanup) |
| **Auth** | `domain/auth` | Authentication, authorization, rate limiting |
| **Protocol** | `domain/protocol` | Swagger/OpenAPI parsing, protocol storage, field mapping |
| **Gateway** | `domain/gateway` | Gateway configuration CRUD, tool management |
| **Admin** | `domain/admin` | Cross-context orchestration for admin UI endpoints |
| **LLM** | `domain/llm` | Embedded LLM client for testing gateway functionality |

## Architecture

DDD Hexagonal Architecture with 7 Maven modules: `types` → `domain` → `case` → `infrastructure` → `api` → `trigger` → `app`

See `CLAUDE.md` for the full architecture description.
