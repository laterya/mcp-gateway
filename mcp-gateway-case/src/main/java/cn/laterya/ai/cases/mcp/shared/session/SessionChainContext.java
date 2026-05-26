package cn.laterya.ai.cases.mcp.shared.session;

import cn.laterya.ai.domain.session.model.McpSchemaVO;
import cn.laterya.ai.domain.session.model.SessionConfigVO;

/**
 * 会话编排链上下文（SSE / Streamable 共用）
 *
 * <p>Streamable HTTP 额外使用 messageBody 和 initializeResponse 字段；
 * SSE 传输时这两个字段为 null。
 */
public class SessionChainContext {

    private String apiKey;
    private String messageBody;
    private SessionConfigVO sessionConfigVO;
    private McpSchemaVO.JSONRPCResponse initializeResponse;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public SessionConfigVO getSessionConfigVO() {
        return sessionConfigVO;
    }

    public void setSessionConfigVO(SessionConfigVO sessionConfigVO) {
        this.sessionConfigVO = sessionConfigVO;
    }

    public McpSchemaVO.JSONRPCResponse getInitializeResponse() {
        return initializeResponse;
    }

    public void setInitializeResponse(McpSchemaVO.JSONRPCResponse initializeResponse) {
        this.initializeResponse = initializeResponse;
    }

}
