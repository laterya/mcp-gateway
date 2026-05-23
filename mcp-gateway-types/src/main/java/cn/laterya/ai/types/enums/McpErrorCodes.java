package cn.laterya.ai.types.enums;

/**
 * MCP JSON-RPC 2.0 协议错误码
 *
 * <p>分两部分：
 * <ul>
 *   <li>-32700 ~ -32603：JSON-RPC 2.0 标准错误</li>
 *   <li>-32000 ~ -32099：MCP 协议扩展错误</li>
 * </ul>
 */
public final class McpErrorCodes {

    // JSON-RPC 2.0 标准错误
    public static final int PARSE_ERROR = -32700;
    public static final int INVALID_REQUEST = -32600;
    public static final int METHOD_NOT_FOUND = -32601;
    public static final int INVALID_PARAMS = -32602;
    public static final int INTERNAL_ERROR = -32603;

    // MCP 协议扩展错误
    public static final int SESSION_NOT_FOUND = -32000;
    public static final int SESSION_EXPIRED = -32001;
    public static final int SERVER_SHUTTING_DOWN = -32002;
    public static final int TOOL_NOT_FOUND = -32003;
    public static final int TOOL_EXECUTION_FAILED = -32004;

    private McpErrorCodes() {
    }

}
