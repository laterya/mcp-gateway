package cn.laterya.ai.cases.mcp.sse.message.node;

import cn.laterya.ai.cases.mcp.sse.message.AbstractMessageChainNode;
import cn.laterya.ai.cases.mcp.sse.message.MessageChainContext;
import cn.laterya.ai.domain.session.model.SessionConfigVO;
import cn.laterya.ai.domain.session.model.entity.HandleMessageCommandEntity;
import cn.laterya.ai.domain.session.service.ISessionManagementService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * SSE 传输 — 消息处理会话节点
 */
@Slf4j
@Component("sseMessageSessionNode")
public class MessageSessionNode extends AbstractMessageChainNode {

    @Resource
    private ISessionManagementService sessionManagementService;

    @Override
    protected ResponseEntity<Void> doHandle(HandleMessageCommandEntity command, MessageChainContext context) {
        log.info("消息处理 mcp message SessionNode gatewayId:{} sessionId:{}", command.getGatewayId(), command.getSessionId());

        SessionConfigVO sessionConfigVO = sessionManagementService.getSession(command.getSessionId());
        if (null == sessionConfigVO) {
            log.warn("会话不存在或已过期 gatewayId:{} sessionId:{}", command.getGatewayId(), command.getSessionId());
            return ResponseEntity.notFound().build();
        }

        context.setSessionConfigVO(sessionConfigVO);
        return fireNext(command, context);
    }

}
