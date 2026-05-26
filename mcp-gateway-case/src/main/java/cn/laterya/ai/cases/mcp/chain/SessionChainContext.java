package cn.laterya.ai.cases.mcp.chain;

import cn.laterya.ai.domain.session.model.McpSchemaVO;
import cn.laterya.ai.domain.session.model.SessionConfigVO;
import lombok.Data;

/**
 * 会话编排链统一上下文（SSE + Streamable HTTP 共用）
 *
 * <p>字段说明：
 * <ul>
 *   <li>apiKey — 两种传输均使用</li>
 *   <li>messageBody / initializeResponse — 仅 Streamable HTTP 使用</li>
 *   <li>sessionConfigVO — 两种传输均使用</li>
 * </ul>
 */
@Data
public class SessionChainContext {

    private String apiKey;
    private String messageBody;
    private SessionConfigVO sessionConfigVO;
    private McpSchemaVO.JSONRPCResponse initializeResponse;

}
