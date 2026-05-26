package cn.laterya.ai.cases.mcp.sse.chain.node;

import cn.laterya.ai.cases.mcp.sse.chain.AbstractSessionChainNode;
import cn.laterya.ai.cases.mcp.sse.chain.SessionChainContext;
import cn.laterya.ai.domain.session.model.SessionConfigVO;
import cn.laterya.ai.domain.session.service.ISessionManagementService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * SSE 传输 — 创建会话节点
 */
@Slf4j
@Component("sseCreateSessionNode")
public class CreateSessionNode extends AbstractSessionChainNode {

    @Resource
    private ISessionManagementService sessionManagementService;

    @Override
    protected Flux<ServerSentEvent<String>> doHandle(String gatewayId, SessionChainContext context) {
        log.info("创建会话 gatewayId:{}", gatewayId);

        // SSE 传输使用带 apiKey 的重载，会推送 endpoint 事件
        SessionConfigVO sessionConfigVO = sessionManagementService.createSession(gatewayId, context.getApiKey());
        context.setSessionConfigVO(sessionConfigVO);

        return fireNext(gatewayId, context);
    }

}
