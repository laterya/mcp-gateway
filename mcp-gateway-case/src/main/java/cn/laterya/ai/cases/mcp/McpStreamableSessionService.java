package cn.laterya.ai.cases.mcp;

import cn.laterya.ai.cases.mcp.streamable.AbstractStreamableChainNode;
import cn.laterya.ai.cases.mcp.streamable.StreamableChainContext;
import cn.laterya.ai.cases.mcp.streamable.node.StreamableAuthNode;
import cn.laterya.ai.cases.mcp.streamable.node.StreamableHandlerNode;
import cn.laterya.ai.cases.mcp.streamable.node.StreamableSessionNode;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Streamable HTTP 会话编排服务 —— 组装并执行责任链
 *
 * <p>链路：StreamableAuthNode → StreamableSessionNode → StreamableHandlerNode
 */
@Slf4j
@Service
public class McpStreamableSessionService implements IMcpStreamableSessionService {

    @Resource
    private StreamableAuthNode streamableAuthNode;
    @Resource
    private StreamableSessionNode streamableSessionNode;
    @Resource
    private StreamableHandlerNode streamableHandlerNode;

    private AbstractStreamableChainNode chain;

    @PostConstruct
    public void initChain() {
        chain = streamableAuthNode;
        streamableAuthNode.linkWith(streamableSessionNode)
                .linkWith(streamableHandlerNode);
        log.info("Streamable HTTP 编排链初始化完成: AuthNode → SessionNode → HandlerNode");
    }

    @Override
    public StreamableChainContext handleRequest(String gatewayId, String apiKey, String sessionId, String messageBody) {
        StreamableChainContext context = new StreamableChainContext()
                .setApiKey(apiKey)
                .setSessionId(sessionId)
                .setMessageBody(messageBody);
        chain.handle(gatewayId, context);
        return context;
    }

}
