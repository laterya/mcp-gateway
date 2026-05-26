package cn.laterya.ai.cases.mcp.streamable.session.node;

import cn.laterya.ai.cases.mcp.chain.AbstractChainNode;
import cn.laterya.ai.cases.mcp.chain.SessionChainContext;
import cn.laterya.ai.domain.session.service.ISessionManagementService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Streamable HTTP — 创建会话节点
 *
 * <p>调用 createSession(gatewayId) 而非 createSession(gatewayId, apiKey)，
 * 因为 Streamable HTTP 不需要推送 endpoint 事件。
 */
@Slf4j
@Component("streamableCreateSessionNode")
public class StreamableCreateSessionNode extends AbstractChainNode<SessionChainContext, Void> {

    @Resource
    private ISessionManagementService sessionManagementService;

    @Override
    protected Void doHandle(SessionChainContext context) {
        log.info("Streamable HTTP 创建会话 gatewayId:{}", context.getGatewayId());

        var sessionConfigVO = sessionManagementService.createSession(context.getGatewayId());
        context.setSessionConfigVO(sessionConfigVO);

        fireNext(context);
        return null;
    }

}
