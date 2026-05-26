package cn.laterya.ai.cases.mcp.streamable.session.node;

import cn.laterya.ai.cases.mcp.streamable.session.AbstractStreamableSessionChainNode;
import cn.laterya.ai.cases.mcp.streamable.session.StreamableSessionChainContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Streamable HTTP — 根节点，日志记录
 */
@Slf4j
@Component("streamableSessionRootNode")
public class StreamableRootNode extends AbstractStreamableSessionChainNode {

    @Override
    protected void doHandle(String gatewayId, StreamableSessionChainContext context) {
        log.info("MCP Streamable HTTP 会话编排开始 gatewayId:{}", gatewayId);
        fireNext(gatewayId, context);
    }

}
