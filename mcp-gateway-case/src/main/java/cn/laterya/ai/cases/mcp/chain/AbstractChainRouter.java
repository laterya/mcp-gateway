package cn.laterya.ai.cases.mcp.chain;

/**
 * 泛型责任链框架
 *
 * <p>借鉴 xfg-wrench StrategyRouter 的三泛型参数设计：
 * <ul>
 *   <li>T — 请求参数类型（如 String gatewayId、HandleMessageCommandEntity）</li>
 *   <li>D — 链上下文类型（如 SessionChainContext、MessageChainContext）</li>
 *   <li>R — 返回类型（如 Flux、ResponseEntity、Void）</li>
 * </ul>
 *
 * <p>子类实现 {@link #doHandle} 编写节点逻辑，通过 {@link #fireNext} 传递到下一节点。
 * 末端节点不调用 fireNext，直接返回结果。
 *
 * @param <T> 请求参数类型
 * @param <D> 链上下文类型
 * @param <R> 返回类型
 */
public abstract class AbstractChainRouter<T, D, R> {

    private AbstractChainRouter<T, D, R> next;

    public AbstractChainRouter<T, D, R> linkWith(AbstractChainRouter<T, D, R> next) {
        this.next = next;
        return next;
    }

    public R handle(T request, D context) {
        return doHandle(request, context);
    }

    protected abstract R doHandle(T request, D context);

    protected R fireNext(T request, D context) {
        if (next == null) {
            return defaultResponse();
        }
        return next.handle(request, context);
    }

    protected R defaultResponse() {
        return null;
    }

}
