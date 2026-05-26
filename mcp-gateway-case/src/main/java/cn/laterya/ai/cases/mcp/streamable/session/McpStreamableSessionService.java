package cn.laterya.ai.cases.mcp.streamable.session;

import cn.laterya.ai.cases.mcp.chain.AbstractChainRouter;
import cn.laterya.ai.cases.mcp.chain.SessionChainContext;
import cn.laterya.ai.cases.mcp.streamable.session.node.StreamableCreateSessionNode;
import cn.laterya.ai.cases.mcp.streamable.session.node.StreamableInitResponseNode;
import cn.laterya.ai.cases.mcp.streamable.session.node.StreamableRootNode;
import cn.laterya.ai.cases.mcp.streamable.session.node.StreamableVerifyNode;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Streamable HTTP — 会话编排服务
 *
 * <p>链路：StreamableRootNode → StreamableVerifyNode → StreamableCreateSessionNode → StreamableInitResponseNode
 */
@Slf4j
@Service("streamableMcpSessionService")
public class McpStreamableSessionService implements IMcpStreamableSessionService {

    @Resource(name = "streamableSessionRootNode")
    private StreamableRootNode rootNode;
    @Resource(name = "streamableVerifyNode")
    private StreamableVerifyNode verifyNode;
    @Resource(name = "streamableCreateSessionNode")
    private StreamableCreateSessionNode createSessionNode;
    @Resource(name = "streamableInitResponseNode")
    private StreamableInitResponseNode initResponseNode;

    private AbstractChainRouter<String, SessionChainContext, Void> chain;

    @PostConstruct
    public void initChain() {
        chain = rootNode;
        rootNode.linkWith(verifyNode)
                .linkWith(createSessionNode)
                .linkWith(initResponseNode);

        log.info("MCP Streamable HTTP 会话编排链初始化完成: RootNode → VerifyNode → CreateSessionNode → InitResponseNode");
    }

    @Override
    public SessionChainContext handleInitialize(String gatewayId, String apiKey, String messageBody) {
        SessionChainContext context = new SessionChainContext();
        context.setApiKey(apiKey);
        context.setMessageBody(messageBody);
        chain.handle(gatewayId, context);
        return context;
    }

}
