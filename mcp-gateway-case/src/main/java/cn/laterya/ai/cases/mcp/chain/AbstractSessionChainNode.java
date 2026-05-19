package cn.laterya.ai.cases.mcp.chain;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

/**
 * 会话编排链抽象节点 —— 责任链模式的核心
 *
 * <p>设计思路（不引入外部框架，手写实现）：
 * <pre>
 * 每个节点持有 next 指针，形成链表：
 *
 * RootNode → VerifyNode → CreateSessionNode → SseResponseNode
 *   日志        验证          创建会话           返回SSE流
 *
 * 调用方式：
 *   root.handle(gatewayId, context)
 *     → RootNode.doHandle()     执行自身逻辑
 *     → fireNext()              触发下一个节点
 *     → VerifyNode.doHandle()   ...
 *     → ...链式调用直到末端节点
 * </pre>
 *
 * <p>为什么不直接在 Service 里顺序调用各方法？
 * 1. 解耦：每个节点是独立 Spring Bean，可单独替换/跳过
 * 2. 扩展：新增节点只需新建类 + 修改链路组装，不影响已有节点
 * 3. 切量：通过 get() 动态返回下一个节点，可实现灰度/AB 等路由逻辑
 */
public abstract class AbstractSessionChainNode {

    /** 链表 next 指针 */
    private AbstractSessionChainNode next;

    /**
     * 组装链路 —— 返回被链接的节点，支持链式调用：
     * <pre>
     * root.linkWith(verify)
     *     .linkWith(create)
     *     .linkWith(response);
     * </pre>
     */
    public AbstractSessionChainNode linkWith(AbstractSessionChainNode next) {
        this.next = next;
        return next;
    }

    /** 链路入口 */
    public Flux<ServerSentEvent<String>> handle(String gatewayId, SessionChainContext context) {
        return doHandle(gatewayId, context);
    }

    /** 触发下一个节点，末端节点不调用此方法 */
    protected Flux<ServerSentEvent<String>> fireNext(String gatewayId, SessionChainContext context) {
        if (next == null) {
            return Flux.empty();
        }
        return next.handle(gatewayId, context);
    }

    /** 子类实现具体的节点逻辑 */
    protected abstract Flux<ServerSentEvent<String>> doHandle(String gatewayId, SessionChainContext context);

}
