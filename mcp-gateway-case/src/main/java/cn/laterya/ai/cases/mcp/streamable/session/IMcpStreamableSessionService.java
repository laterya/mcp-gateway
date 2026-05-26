package cn.laterya.ai.cases.mcp.streamable.session;

import cn.laterya.ai.cases.mcp.chain.SessionChainContext;

/**
 * Streamable HTTP — 会话服务接口
 *
 * <p>处理 POST initialize 请求：创建会话 + 生成 JSON-RPC 初始化响应。
 * Controller 从返回的上下文中取出 sessionId 和 response 构建最终 HTTP 响应。
 */
public interface IMcpStreamableSessionService {

    /**
     * 处理 Streamable HTTP 初始化请求
     *
     * @param gatewayId   网关标识
     * @param apiKey      API 密钥
     * @param messageBody JSON-RPC 请求体（initialize）
     * @return 包含 sessionConfigVO 和 initializeResponse 的上下文
     */
    SessionChainContext handleInitialize(String gatewayId, String apiKey, String messageBody);

}
