package cn.laterya.ai.cases.mcp.streamable.session.node;

import cn.laterya.ai.cases.mcp.shared.session.SessionChainContext;
import cn.laterya.ai.cases.mcp.streamable.session.AbstractStreamableSessionChainNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Streamable HTTP — 根节点，日志记录
 */
@Slf4j
@Component("streamableSessionRootNode")
public class StreamableRootNode extends AbstractStreamableSessionChainNode {

    @Override
    protected void doHandle(String gatewayId, SessionChainContext context) {
        log.info("MCP Streamable HTTP 会话编排开始 gatewayId:{}", gatewayId);
        fireNext(gatewayId, context);
    }

}
