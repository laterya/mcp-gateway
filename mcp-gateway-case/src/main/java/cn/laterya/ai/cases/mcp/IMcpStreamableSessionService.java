package cn.laterya.ai.cases.mcp;

import cn.laterya.ai.cases.mcp.streamable.StreamableChainContext;

/**
 * Streamable HTTP 会话编排接口
 */
public interface IMcpStreamableSessionService {

    /**
     * 处理 Streamable HTTP 请求
     *
     * @param gatewayId 网关标识
     * @param apiKey    API 密钥（从 Authorization: Bearer 头提取）
     * @param sessionId 会话 ID（从 Mcp-Session-Id 头获取，initialize 时为 null）
     * @param messageBody JSON-RPC 请求体
     * @return 链上下文（含响应和 sessionId）
     */
    StreamableChainContext handleRequest(String gatewayId, String apiKey, String sessionId, String messageBody);

}
