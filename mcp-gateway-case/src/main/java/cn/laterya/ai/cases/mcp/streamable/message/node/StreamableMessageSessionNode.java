package cn.laterya.ai.cases.mcp.streamable.message.node;

import cn.laterya.ai.cases.mcp.streamable.message.AbstractStreamableMessageChainNode;
import cn.laterya.ai.cases.mcp.streamable.message.StreamableMessageChainContext;
import cn.laterya.ai.domain.session.model.SessionConfigVO;
import cn.laterya.ai.domain.session.model.entity.HandleMessageCommandEntity;
import cn.laterya.ai.domain.session.service.ISessionManagementService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Streamable HTTP — 消息处理会话节点
 */
@Slf4j
@Component("streamableMessageSessionNode")
public class StreamableMessageSessionNode extends AbstractStreamableMessageChainNode {

    @Resource
    private ISessionManagementService sessionManagementService;

    @Override
    protected ResponseEntity<Void> doHandle(HandleMessageCommandEntity command, StreamableMessageChainContext context) {
        log.info("Streamable HTTP 消息处理 SessionNode gatewayId:{} sessionId:{}", command.getGatewayId(), command.getSessionId());

        SessionConfigVO sessionConfigVO = sessionManagementService.getSession(command.getSessionId());
        if (null == sessionConfigVO) {
            log.warn("会话不存在或已过期 gatewayId:{} sessionId:{}", command.getGatewayId(), command.getSessionId());
            return ResponseEntity.notFound().build();
        }

        context.setSessionConfigVO(sessionConfigVO);
        return fireNext(command, context);
    }

}
