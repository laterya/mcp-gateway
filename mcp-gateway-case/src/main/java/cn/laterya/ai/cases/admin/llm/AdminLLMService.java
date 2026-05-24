package cn.laterya.ai.cases.admin.llm;

import cn.laterya.ai.cases.admin.IAdminLLMService;
import cn.laterya.ai.domain.llm.service.ILLMService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdminLLMService implements IAdminLLMService {

    @Resource
    private ILLMService llmService;

    @Value("${mcp-gateway.base-url:http://127.0.0.1:8090}")
    private String baseUrl;

    @Value("${server.servlet.context-path:/api-gateway}")
    private String contextPath;

    @Override
    public String testCallGateway(String gatewayId, String message, String apiKey, long timeout, boolean reload) {
        log.info("测试调用网关 gatewayId:{} timeout:{} reload:{}", gatewayId, timeout, reload);

        // 拼接与 McpGatewayController 一致的 SSE 端点路径（contextPath 以 / 开头，不再额外加 /）
        String sseEndpoint = contextPath + "/" + gatewayId + "/mcp/sse";
        if (apiKey != null && !apiKey.isEmpty()) {
            sseEndpoint += "?api_key=" + apiKey;
        }

        return llmService.call(baseUrl, sseEndpoint, message, timeout, reload);
    }
}
