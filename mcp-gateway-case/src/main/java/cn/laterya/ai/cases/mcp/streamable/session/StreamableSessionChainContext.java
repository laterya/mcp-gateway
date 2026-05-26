package cn.laterya.ai.cases.mcp.streamable.session;

import cn.laterya.ai.domain.session.model.McpSchemaVO;
import cn.laterya.ai.domain.session.model.SessionConfigVO;

/**
 * Streamable HTTP — 会话编排链上下文
 *
 * <p>与 SSE 的 SessionChainContext 的区别：
 * - 新增 messageBody（原始 JSON-RPC 请求体）和 initializeResponse（JSON-RPC 初始化响应）
 * - initialize 响应通过 HTTP body 返回，需要 Controller 从上下文取出 sessionId 和 response
 */
public class StreamableSessionChainContext {

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
