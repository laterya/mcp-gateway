package cn.laterya.ai.cases.mcp;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

/**
 * MCP 会话服务接口（case 层对外暴露的能力）
 *
 * <p>由 trigger 层的 Controller 调用，
 * 内部通过责任链编排多个 domain 服务完成完整的会话创建流程。
 */
public interface IMcpSessionService {

    /**
     * 创建 MCP 会话并返回 SSE 流
     *
     * @param gatewayId 网关标识
     * @return SSE 流式响应
     */
    Flux<ServerSentEvent<String>> createMcpSession(String gatewayId);

}
