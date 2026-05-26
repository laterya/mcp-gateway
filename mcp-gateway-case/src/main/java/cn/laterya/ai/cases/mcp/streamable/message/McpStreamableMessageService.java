package cn.laterya.ai.cases.mcp.streamable.message;

import cn.laterya.ai.cases.mcp.streamable.message.node.StreamableMessageHandlerNode;
import cn.laterya.ai.cases.mcp.streamable.message.node.StreamableMessageRootNode;
import cn.laterya.ai.cases.mcp.streamable.message.node.StreamableMessageSessionNode;
import cn.laterya.ai.domain.session.model.entity.HandleMessageCommandEntity;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Streamable HTTP — 消息处理编排服务
 *
 * <p>链路：StreamableMessageRootNode → StreamableMessageSessionNode → StreamableMessageHandlerNode
 */
@Slf4j
@Service("streamableMcpMessageService")
public class McpStreamableMessageService implements IMcpStreamableMessageService {

    @Resource(name = "streamableMessageRootNode")
    private StreamableMessageRootNode messageRootNode;
    @Resource(name = "streamableMessageSessionNode")
    private StreamableMessageSessionNode messageSessionNode;
    @Resource(name = "streamableMessageHandlerNode")
    private StreamableMessageHandlerNode messageHandlerNode;

    private AbstractStreamableMessageChainNode chain;

    @PostConstruct
    public void initChain() {
        chain = messageRootNode;
        messageRootNode.linkWith(messageSessionNode)
                .linkWith(messageHandlerNode);

        log.info("MCP Streamable HTTP 消息编排链初始化完成: RootNode → SessionNode → HandlerNode");
    }

    @Override
    public ResponseEntity<Void> handleMessage(HandleMessageCommandEntity commandEntity) throws Exception {
        return chain.handle(commandEntity, new StreamableMessageChainContext());
    }

}
