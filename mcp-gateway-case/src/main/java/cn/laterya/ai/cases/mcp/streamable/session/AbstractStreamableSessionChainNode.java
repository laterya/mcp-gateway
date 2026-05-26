package cn.laterya.ai.cases.mcp.streamable.session;

import cn.laterya.ai.cases.mcp.chain.AbstractChainRouter;
import cn.laterya.ai.cases.mcp.chain.SessionChainContext;

/**
 * Streamable HTTP — 会话编排链抽象节点
 *
 * <p>继承泛型链框架，返回类型为 Void（结果通过 context 传递）。
 * 与 SSE 的 AbstractSessionChainNode 的区别仅在于返回类型。
 */
public abstract class AbstractStreamableSessionChainNode extends AbstractChainRouter<String, SessionChainContext, Void> {

}
