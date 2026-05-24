package cn.laterya.ai.domain.llm.service;

public interface ILLMService {

    /**
     * 调用 LLM（含 MCP 工具回调）
     *
     * @param baseUrl     网关服务基础地址（如 http://127.0.0.1:8090）
     * @param sseEndpoint SSE 端点路径（如 /api-gateway/gateway_001/mcp/sse?api_key=xxx）
     * @param message     用户发送的自然语言消息
     * @param timeout     MCP 客户端超时（秒）
     * @param reload      是否强制重新构建 ChatClient（工具变更后需要）
     * @return LLM 响应文本
     */
    String call(String baseUrl, String sseEndpoint, String message, long timeout, boolean reload);
}
