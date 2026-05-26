package cn.laterya.ai.cases.mcp.sse.node;

import cn.laterya.ai.cases.mcp.chain.AbstractChainNode;
import cn.laterya.ai.cases.mcp.chain.SessionChainContext;
import cn.laterya.ai.domain.session.service.ISessionManagementService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * SSE 传输 — 终端节点，构建 SSE 流 + 心跳 + 断连清理
 */
@Slf4j
@Component("sseResponseNode")
public class SseResponseNode extends AbstractChainNode<SessionChainContext, Flux<ServerSentEvent<String>>> {

    @Resource
    private ISessionManagementService sessionManagementService;

    @Override
    protected Flux<ServerSentEvent<String>> doHandle(SessionChainContext context) {
        var sessionConfigVO = context.getSessionConfigVO();

        Flux<ServerSentEvent<String>> heartbeat = Flux.interval(Duration.ofSeconds(60))
                .map(i -> ServerSentEvent.<String>builder()
                        .comment("ping")
                        .build());

        return Flux.merge(sessionConfigVO.getSink().asFlux(), heartbeat)
                .doOnCancel(() -> {
                    log.info("SSE 连接取消 sessionId:{}", sessionConfigVO.getSessionId());
                    sessionManagementService.removeSession(sessionConfigVO.getSessionId());
                })
                .doOnTerminate(() -> {
                    log.info("SSE 连接终止 sessionId:{}", sessionConfigVO.getSessionId());
                    sessionManagementService.removeSession(sessionConfigVO.getSessionId());
                });
    }

}
