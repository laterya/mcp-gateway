package cn.laterya.ai.cases.mcp.streamable.message;

import cn.laterya.ai.domain.session.model.entity.HandleMessageCommandEntity;
import org.springframework.http.ResponseEntity;

/**
 * Streamable HTTP — 消息编排链抽象节点
 */
public abstract class AbstractStreamableMessageChainNode {

    private AbstractStreamableMessageChainNode next;

    public AbstractStreamableMessageChainNode linkWith(AbstractStreamableMessageChainNode next) {
        this.next = next;
        return next;
    }

    public ResponseEntity<Void> handle(HandleMessageCommandEntity command, StreamableMessageChainContext context) {
        return doHandle(command, context);
    }

    protected ResponseEntity<Void> fireNext(HandleMessageCommandEntity command, StreamableMessageChainContext context) {
        if (next == null) {
            return ResponseEntity.accepted().build();
        }
        return next.handle(command, context);
    }

    protected abstract ResponseEntity<Void> doHandle(HandleMessageCommandEntity command, StreamableMessageChainContext context);

}
