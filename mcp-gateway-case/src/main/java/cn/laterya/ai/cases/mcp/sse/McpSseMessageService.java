package cn.laterya.ai.cases.mcp.sse;

import cn.laterya.ai.cases.mcp.sse.message.AbstractMessageChainNode;
import cn.laterya.ai.cases.mcp.sse.message.MessageChainContext;
import cn.laterya.ai.cases.mcp.sse.message.node.MessageHandlerNode;
import cn.laterya.ai.cases.mcp.sse.message.node.MessageRootNode;
import cn.laterya.ai.cases.mcp.sse.message.node.MessageSessionNode;
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

    @Resource(name = "sseMessageRootNode")
    private MessageRootNode messageRootNode;
    @Resource(name = "sseMessageSessionNode")
    private MessageSessionNode messageSessionNode;
    @Resource(name = "sseMessageHandlerNode")
    private MessageHandlerNode messageHandlerNode;

    private AbstractMessageChainNode chain;

    @PostConstruct
    public void initChain() {
        chain = messageRootNode;
        messageRootNode.linkWith(messageSessionNode)
                .linkWith(messageHandlerNode);

        log.info("MCP SSE 消息编排链初始化完成: MessageRootNode → MessageSessionNode → MessageHandlerNode");
    }

    @Override
    public ResponseEntity<Void> handleMessage(HandleMessageCommandEntity commandEntity) throws Exception {
        return chain.handle(commandEntity, new MessageChainContext());
    }

}
