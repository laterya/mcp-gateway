package cn.laterya.ai.cases.mcp.sse.session;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

/**
 * SSE 传输 — 会话服务接口
 *
 * <p>由 trigger 层的 McpGatewayController 调用，
 * 内部通过责任链编排多个 domain 服务完成完整的会话创建流程。
 */
public interface IMcpSseSessionService {

    Flux<ServerSentEvent<String>> createMcpSession(String gatewayId, String apiKey);

}
