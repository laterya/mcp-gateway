package cn.laterya.ai.cases.mcp.sse.session.node;

import cn.laterya.ai.cases.mcp.chain.SessionChainContext;
import cn.laterya.ai.cases.mcp.sse.session.AbstractSessionChainNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * SSE 传输 — 根节点，日志记录后放行
 */
@Slf4j
@Component("sseSessionRootNode")
public class RootNode extends AbstractSessionChainNode {

    @Override
    protected Flux<ServerSentEvent<String>> doHandle(String gatewayId, SessionChainContext context) {
        log.info("MCP SSE 会话编排开始 gatewayId:{}", gatewayId);
        return fireNext(gatewayId, context);
    }

}
