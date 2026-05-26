package cn.laterya.ai.cases.mcp.sse.chain;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

/**
 * SSE 传输 — 会话编排链抽象节点
 *
 * <p>链路：RootNode → VerifyNode → CreateSessionNode → SseResponseNode
 */
public abstract class AbstractSessionChainNode {

    private AbstractSessionChainNode next;

    public AbstractSessionChainNode linkWith(AbstractSessionChainNode next) {
        this.next = next;
        return next;
    }

    public Flux<ServerSentEvent<String>> handle(String gatewayId, SessionChainContext context) {
        return doHandle(gatewayId, context);
    }

    protected Flux<ServerSentEvent<String>> fireNext(String gatewayId, SessionChainContext context) {
        if (next == null) {
            return Flux.empty();
        }
        return next.handle(gatewayId, context);
    }

    protected abstract Flux<ServerSentEvent<String>> doHandle(String gatewayId, SessionChainContext context);

}
