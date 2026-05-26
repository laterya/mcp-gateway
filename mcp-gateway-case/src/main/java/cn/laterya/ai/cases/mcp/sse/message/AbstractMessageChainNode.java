package cn.laterya.ai.cases.mcp.sse.message;

import cn.laterya.ai.domain.session.model.entity.HandleMessageCommandEntity;
import org.springframework.http.ResponseEntity;

/**
 * SSE 传输 — 消息编排链抽象节点
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
