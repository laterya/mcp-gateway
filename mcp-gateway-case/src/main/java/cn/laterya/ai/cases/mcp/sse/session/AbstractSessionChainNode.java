package cn.laterya.ai.cases.mcp.sse.session;

import cn.laterya.ai.cases.mcp.chain.AbstractChainRouter;
import cn.laterya.ai.cases.mcp.chain.SessionChainContext;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

/**
 * SSE 传输 — 会话编排链抽象节点
 *
 * <p>链路：RootNode → VerifyNode → CreateSessionNode → SseResponseNode
 */
public abstract class AbstractSessionChainNode extends AbstractChainRouter<String, SessionChainContext, Flux<ServerSentEvent<String>>> {

    @Override
    protected Flux<ServerSentEvent<String>> defaultResponse() {
        return Flux.empty();
    }

}
