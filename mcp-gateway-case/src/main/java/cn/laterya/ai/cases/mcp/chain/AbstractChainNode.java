package cn.laterya.ai.cases.mcp.chain;

/**
 * 泛型链路抽象节点
 *
 * <p>统一 SSE / Streamable HTTP 两种传输的责任链基础设施。
 * C = 上下文类型，R = 返回类型。
 *
 * @param <C> 链路上下文
 * @param <R> 处理结果类型
 */
public abstract class AbstractChainNode<C, R> {

    private AbstractChainNode<C, R> next;

    public AbstractChainNode<C, R> linkWith(AbstractChainNode<C, R> next) {
        this.next = next;
        return next;
    }

    public R handle(C context) {
        return doHandle(context);
    }

    protected R fireNext(C context) {
        if (next == null) {
            return null;
        }
        return next.handle(context);
    }

    protected abstract R doHandle(C context);

}
