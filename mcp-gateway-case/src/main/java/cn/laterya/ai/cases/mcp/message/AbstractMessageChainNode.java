package cn.laterya.ai.cases.mcp.message;

import cn.laterya.ai.domain.session.model.entity.HandleMessageCommandEntity;
import org.springframework.http.ResponseEntity;

/**
 * 消息编排链抽象节点 —— 责任链模式
 *
 * <p>与 Session 编排链（AbstractSessionChainNode）同模式，独立维护。
 * 消息处理的链路：MessageRootNode → MessageSessionNode → MessageHandlerNode
 */
public abstract class AbstractMessageChainNode {

    private AbstractMessageChainNode next;

    public AbstractMessageChainNode linkWith(AbstractMessageChainNode next) {
        this.next = next;
        return next;
    }

    public ResponseEntity<Void> handle(HandleMessageCommandEntity command, MessageChainContext context) {
        return doHandle(command, context);
    }

    protected ResponseEntity<Void> fireNext(HandleMessageCommandEntity command, MessageChainContext context) {
        if (next == null) {
            return ResponseEntity.accepted().build();
        }
        return next.handle(command, context);
    }

    protected abstract ResponseEntity<Void> doHandle(HandleMessageCommandEntity command, MessageChainContext context);

}
