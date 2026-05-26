package cn.laterya.ai.cases.mcp.sse;

import cn.laterya.ai.cases.mcp.sse.chain.AbstractSessionChainNode;
import cn.laterya.ai.cases.mcp.sse.chain.SessionChainContext;
import cn.laterya.ai.cases.mcp.sse.chain.node.CreateSessionNode;
import cn.laterya.ai.cases.mcp.sse.chain.node.RootNode;
import cn.laterya.ai.cases.mcp.sse.chain.node.SseResponseNode;
import cn.laterya.ai.cases.mcp.sse.chain.node.VerifyNode;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * SSE 传输 — 会话编排服务
 *
 * <p>链路：RootNode → VerifyNode → CreateSessionNode → SseResponseNode
 */
@Slf4j
@Service("sseMcpSessionService")
public class McpSseSessionService implements IMcpSseSessionService {

    @Resource(name = "sseSessionRootNode")
    private RootNode rootNode;
    @Resource(name = "sseVerifyNode")
    private VerifyNode verifyNode;
    @Resource(name = "sseCreateSessionNode")
    private CreateSessionNode createSessionNode;
    @Resource(name = "sseSseResponseNode")
    private SseResponseNode sseResponseNode;

    private AbstractSessionChainNode chain;

    @PostConstruct
    public void initChain() {
        chain = rootNode;
        rootNode.linkWith(verifyNode)
                .linkWith(createSessionNode)
                .linkWith(sseResponseNode);

        log.info("MCP SSE 会话编排链初始化完成: RootNode → VerifyNode → CreateSessionNode → SseResponseNode");
    }

    @Override
    public Flux<ServerSentEvent<String>> createMcpSession(String gatewayId, String apiKey) {
        SessionChainContext context = new SessionChainContext();
        context.setApiKey(apiKey);
        return chain.handle(gatewayId, context);
    }

}
