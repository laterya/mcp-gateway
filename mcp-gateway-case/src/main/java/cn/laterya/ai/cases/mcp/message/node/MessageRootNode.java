package cn.laterya.ai.cases.mcp.message.node;

import cn.laterya.ai.cases.mcp.message.AbstractMessageChainNode;
import cn.laterya.ai.cases.mcp.message.MessageChainContext;
import cn.laterya.ai.domain.session.model.entity.HandleMessageCommandEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * 消息处理根节点 —— 日志记录 + 异常兜底
 */
@Slf4j
@Component
public class MessageRootNode extends AbstractMessageChainNode {

    @Override
    protected ResponseEntity<Void> doHandle(HandleMessageCommandEntity command, MessageChainContext context) {
        try {
            log.info("消息处理 mcp message RootNode gatewayId:{} sessionId:{}", command.getGatewayId(), command.getSessionId());
            return fireNext(command, context);
        } catch (Exception e) {
            log.error("消息处理异常 gatewayId:{} sessionId:{}", command.getGatewayId(), command.getSessionId(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

}
