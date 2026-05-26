package cn.laterya.ai.cases.mcp.shared.message;

import cn.laterya.ai.cases.mcp.shared.message.node.MessageHandlerNode;
import cn.laterya.ai.cases.mcp.shared.message.node.MessageRootNode;
import cn.laterya.ai.cases.mcp.shared.message.node.MessageSessionNode;
import cn.laterya.ai.domain.session.model.entity.HandleMessageCommandEntity;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * 消息处理编排服务（SSE / Streamable 共用）
 *
 * <p>链路：MessageRootNode → MessageSessionNode → MessageHandlerNode
 */
@Slf4j
@Service("mcpMessageService")
public class McpMessageService implements IMcpMessageService {

    @Resource(name = "messageRootNode")
    private MessageRootNode messageRootNode;
    @Resource(name = "messageSessionNode")
    private MessageSessionNode messageSessionNode;
    @Resource(name = "messageHandlerNode")
    private MessageHandlerNode messageHandlerNode;

    private AbstractMessageChainNode chain;

    @PostConstruct
    public void initChain() {
        chain = messageRootNode;
        messageRootNode.linkWith(messageSessionNode)
                .linkWith(messageHandlerNode);

        log.info("MCP 消息编排链初始化完成: MessageRootNode → MessageSessionNode → MessageHandlerNode");
    }

    @Override
    public ResponseEntity<Void> handleMessage(HandleMessageCommandEntity commandEntity) throws Exception {
        return chain.handle(commandEntity, new MessageChainContext());
    }

}
