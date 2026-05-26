package cn.laterya.ai.cases.mcp.chain.session;

import cn.laterya.ai.cases.mcp.chain.AbstractChainNode;
import cn.laterya.ai.cases.mcp.chain.SessionChainContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 共享会话链根节点 — 日志记录后放行
 */
@Slf4j
public class SessionRootNode<R> extends AbstractChainNode<SessionChainContext, R> {

    private final String transportName;

    public SessionRootNode(String transportName) {
        this.transportName = transportName;
    }

    @Override
    protected R doHandle(SessionChainContext context) {
        log.info("MCP {} 会话编排开始 gatewayId:{}", transportName, context.getGatewayId());
        return fireNext(context);
    }

}
