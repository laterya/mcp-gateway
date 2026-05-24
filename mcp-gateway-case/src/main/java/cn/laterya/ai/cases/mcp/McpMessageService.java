package cn.laterya.ai.cases.mcp;

import cn.laterya.ai.cases.mcp.message.AbstractMessageChainNode;
import cn.laterya.ai.cases.mcp.message.MessageChainContext;
import cn.laterya.ai.cases.mcp.message.node.MessageHandlerNode;
import cn.laterya.ai.cases.mcp.message.node.MessageRootNode;
import cn.laterya.ai.cases.mcp.message.node.MessageSessionNode;
import cn.laterya.ai.domain.session.model.entity.HandleMessageCommandEntity;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * MCP 消息处理编排服务 —— case 层
 *
 * <p>链路：MessageRootNode → MessageSessionNode → MessageHandlerNode
 * <pre>
 * MessageRootNode      日志记录 + 异常兜底
 *   ↓
 * MessageSessionNode   获取 session 对象，存入上下文
 *   ↓
 * MessageHandlerNode   调用 domain 层处理消息，SSE 推送响应（终端节点）
 * </pre>
 */
@Slf4j
@Service
public class McpMessageService implements IMcpMessageService {

    @Resource
    private MessageRootNode messageRootNode;
    @Resource
    private MessageSessionNode messageSessionNode;
    @Resource
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
