package cn.laterya.ai.cases.admin;

public interface IAdminLLMService {

    /**
     * 测试调用网关 — 拼接 SSE 端点并调用 LLM 服务
     */
    String testCallGateway(String gatewayId, String message, String apiKey, long timeout, boolean reload);
}
