package cn.laterya.ai.cases.mcp.streamable.message;

import cn.laterya.ai.cases.mcp.chain.AbstractChainRouter;
import cn.laterya.ai.cases.mcp.chain.MessageChainContext;
import cn.laterya.ai.domain.session.model.entity.HandleMessageCommandEntity;
import org.springframework.http.ResponseEntity;

/**
 * Streamable HTTP — 消息编排链抽象节点
 */
public abstract class AbstractStreamableMessageChainNode extends AbstractChainRouter<HandleMessageCommandEntity, MessageChainContext, ResponseEntity<Void>> {

    @Override
    protected ResponseEntity<Void> defaultResponse() {
        return ResponseEntity.accepted().build();
    }

}
