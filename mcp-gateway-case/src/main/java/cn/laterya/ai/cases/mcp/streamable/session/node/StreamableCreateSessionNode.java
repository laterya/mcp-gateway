package cn.laterya.ai.cases.mcp.streamable.session.node;

import cn.laterya.ai.cases.mcp.shared.session.SessionChainContext;
import cn.laterya.ai.cases.mcp.streamable.session.AbstractStreamableSessionChainNode;
import cn.laterya.ai.domain.session.model.SessionConfigVO;
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
public class StreamableCreateSessionNode extends AbstractStreamableSessionChainNode {

    @Resource
    private ISessionManagementService sessionManagementService;

    @Override
    protected void doHandle(String gatewayId, SessionChainContext context) {
        log.info("Streamable HTTP 创建会话 gatewayId:{}", gatewayId);

        // 不带 apiKey 的重载 — 不推送 endpoint 事件
        SessionConfigVO sessionConfigVO = sessionManagementService.createSession(gatewayId);
        context.setSessionConfigVO(sessionConfigVO);

        fireNext(gatewayId, context);
    }

}
