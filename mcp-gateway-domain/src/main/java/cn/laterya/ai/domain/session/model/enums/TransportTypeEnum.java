package cn.laterya.ai.domain.session.model.enums;

/**
 * MCP 传输方式枚举
 *
 * <p>SSE 传输的 session 附带 Sink 用于推送响应；Streamable HTTP 传输的 session 无 Sink。
 */
public enum TransportTypeEnum {

    SSE,
    STREAMABLE_HTTP

}
