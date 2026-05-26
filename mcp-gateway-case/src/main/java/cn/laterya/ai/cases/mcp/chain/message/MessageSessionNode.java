package cn.laterya.ai.cases.mcp.chain.message;

import cn.laterya.ai.cases.mcp.chain.AbstractChainNode;
import cn.laterya.ai.cases.mcp.chain.MessageChainContext;
import cn.laterya.ai.domain.session.service.ISessionManagementService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * 共享消息链会话节点 — 查找会话
 */
@Slf4j
@Component("mcpMessageSessionNode")
public class MessageSessionNode extends AbstractChainNode<MessageChainContext, ResponseEntity<Void>> {

    @Resource
    private ISessionManagementService sessionManagementService;

    @Override
    protected ResponseEntity<Void> doHandle(MessageChainContext context) {
        var command = context.getCommand();
        log.info("消息处理 SessionNode gatewayId:{} sessionId:{}", command.getGatewayId(), command.getSessionId());

        var sessionConfigVO = sessionManagementService.getSession(command.getSessionId());
        if (null == sessionConfigVO) {
            log.warn("会话不存在或已过期 gatewayId:{} sessionId:{}", command.getGatewayId(), command.getSessionId());
            return ResponseEntity.notFound().build();
        }

        context.setSessionConfigVO(sessionConfigVO);
        return fireNext(context);
    }

}
