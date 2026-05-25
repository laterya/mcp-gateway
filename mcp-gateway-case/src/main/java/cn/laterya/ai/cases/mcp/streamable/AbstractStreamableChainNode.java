package cn.laterya.ai.cases.mcp.streamable;

/**
 * Streamable HTTP 编排链抽象节点
 *
 * <p>与 SSE 的 AbstractSessionChainNode 类似但返回类型不同：
 * SSE 链返回 Flux<ServerSentEvent>（持续流），
 * Streamable HTTP 链通过 context 传递结果，void 返回。
 */
public abstract class AbstractStreamableChainNode {

    private AbstractStreamableChainNode next;

    public AbstractStreamableChainNode linkWith(AbstractStreamableChainNode next) {
        this.next = next;
        return next;
    }

    public void handle(String gatewayId, StreamableChainContext context) {
        doHandle(gatewayId, context);
    }

    protected void fireNext(String gatewayId, StreamableChainContext context) {
        if (next != null) {
            next.handle(gatewayId, context);
        }
    }

    protected abstract void doHandle(String gatewayId, StreamableChainContext context);

}
