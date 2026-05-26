package cn.laterya.ai.cases.mcp.streamable.session;

/**
 * Streamable HTTP — 会话编排链抽象节点
 *
 * <p>与 SSE 的 AbstractSessionChainNode 的区别：
 * 返回 void（结果通过 context 传递），而非 Flux<ServerSentEvent<String>>。
 * 因为 Streamable HTTP 的 initialize 响应通过 HTTP body 返回，
 * 由 Controller 从 context 中取出 sessionId 和 response 构建最终响应。
 */
public abstract class AbstractStreamableSessionChainNode {

    private AbstractStreamableSessionChainNode next;

    public AbstractStreamableSessionChainNode linkWith(AbstractStreamableSessionChainNode next) {
        this.next = next;
        return next;
    }

    public void handle(String gatewayId, StreamableSessionChainContext context) {
        doHandle(gatewayId, context);
    }

    protected void fireNext(String gatewayId, StreamableSessionChainContext context) {
        if (next != null) {
            next.handle(gatewayId, context);
        }
    }

    protected abstract void doHandle(String gatewayId, StreamableSessionChainContext context);

}
