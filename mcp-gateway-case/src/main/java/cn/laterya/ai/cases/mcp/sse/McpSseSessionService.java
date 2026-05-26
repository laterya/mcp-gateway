package cn.laterya.ai.cases.mcp.sse;

import cn.laterya.ai.cases.mcp.chain.AbstractChainNode;
import cn.laterya.ai.cases.mcp.chain.SessionChainContext;
import cn.laterya.ai.cases.mcp.chain.session.SessionRootNode;
import cn.laterya.ai.cases.mcp.chain.session.SessionVerifyNode;
import cn.laterya.ai.cases.mcp.sse.node.SseCreateSessionNode;
import cn.laterya.ai.cases.mcp.sse.node.SseResponseNode;
import cn.laterya.ai.domain.auth.service.IAuthLicenseService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * SSE 传输 — 会话编排服务
 *
 * <p>链路：SessionRootNode → SessionVerifyNode → SseCreateSessionNode → SseResponseNode
 */
@Slf4j
@Service("sseMcpSessionService")
public class McpSseSessionService implements IMcpSseSessionService {

    @Resource
    private IAuthLicenseService authLicenseService;
    @Resource(name = "sseCreateSessionNode")
    private SseCreateSessionNode createSessionNode;
    @Resource(name = "sseResponseNode")
    private SseResponseNode responseNode;

    private AbstractChainNode<SessionChainContext, Flux<ServerSentEvent<String>>> chain;

    @PostConstruct
    public void initChain() {
        var rootNode = new SessionRootNode<Flux<ServerSentEvent<String>>>("SSE");
        var verifyNode = new SessionVerifyNode<Flux<ServerSentEvent<String>>>(authLicenseService);

        chain = rootNode;
        rootNode.linkWith(verifyNode)
                .linkWith(createSessionNode)
                .linkWith(responseNode);

        log.info("MCP SSE 会话编排链初始化完成: SessionRootNode → SessionVerifyNode → SseCreateSessionNode → SseResponseNode");
    }

    @Override
    public Flux<ServerSentEvent<String>> createMcpSession(String gatewayId, String apiKey) {
        SessionChainContext context = new SessionChainContext();
        context.setGatewayId(gatewayId);
        context.setApiKey(apiKey);
        return chain.handle(context);
    }

}
