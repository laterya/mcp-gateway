package cn.laterya.ai.cases.mcp.sse;

import cn.laterya.ai.cases.mcp.chain.AbstractChainNode;
import cn.laterya.ai.cases.mcp.chain.MessageChainContext;
import cn.laterya.ai.cases.mcp.chain.message.MessageHandlerNode;
import cn.laterya.ai.cases.mcp.chain.message.MessageRootNode;
import cn.laterya.ai.cases.mcp.chain.message.MessageSessionNode;
import cn.laterya.ai.domain.session.model.entity.HandleMessageCommandEntity;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * SSE 传输 — 消息处理编排服务
 *
 * <p>链路：MessageRootNode → MessageSessionNode → MessageHandlerNode
 */
@Slf4j
@Service("sseMcpMessageService")
public class McpSseMessageService implements IMcpSseMessageService {

    @Resource(name = "mcpMessageRootNode")
    private MessageRootNode messageRootNode;
    @Resource(name = "mcpMessageSessionNode")
    private MessageSessionNode messageSessionNode;
    @Resource(name = "mcpMessageHandlerNode")
    private MessageHandlerNode messageHandlerNode;

    private AbstractChainNode<MessageChainContext, ResponseEntity<Void>> chain;

    @PostConstruct
    public void initChain() {
        chain = messageRootNode;
        messageRootNode.linkWith(messageSessionNode)
                .linkWith(messageHandlerNode);

        log.info("MCP 消息编排链初始化完成 (SSE): MessageRootNode → MessageSessionNode → MessageHandlerNode");
    }

    @Override
    public ResponseEntity<Void> handleMessage(HandleMessageCommandEntity commandEntity) throws Exception {
        return chain.handle(new MessageChainContext(commandEntity));
    }

}
