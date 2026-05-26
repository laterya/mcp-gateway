package cn.laterya.ai.cases.mcp.sse.node;

import cn.laterya.ai.cases.mcp.chain.AbstractChainNode;
import cn.laterya.ai.cases.mcp.chain.SessionChainContext;
import cn.laterya.ai.domain.session.service.ISessionManagementService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * SSE 传输 — 创建会话节点（使用带 apiKey 的重载，会推送 endpoint 事件）
 */
@Slf4j
@Component("sseCreateSessionNode")
public class SseCreateSessionNode extends AbstractChainNode<SessionChainContext, Flux<ServerSentEvent<String>>> {

    @Resource
    private ISessionManagementService sessionManagementService;

    @Override
    protected Flux<ServerSentEvent<String>> doHandle(SessionChainContext context) {
        log.info("创建会话 gatewayId:{}", context.getGatewayId());

        var sessionConfigVO = sessionManagementService.createSession(context.getGatewayId(), context.getApiKey());
        context.setSessionConfigVO(sessionConfigVO);

        return fireNext(context);
    }

}
