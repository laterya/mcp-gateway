package cn.laterya.ai.cases.mcp.chain;

import cn.laterya.ai.domain.session.model.McpSchemaVO;
import cn.laterya.ai.domain.session.model.SessionConfigVO;

/**
 * 统一会话编排链上下文
 *
 * <p>SSE 和 Streamable HTTP 共用。Streamable 特有字段（messageBody、initializeResponse）在 SSE 链路中为 null。
 */
public class SessionChainContext {

    private String gatewayId;
    private String apiKey;
    private String messageBody;
    private SessionConfigVO sessionConfigVO;
    private McpSchemaVO.JSONRPCResponse initializeResponse;

    public String getGatewayId() { return gatewayId; }
    public void setGatewayId(String gatewayId) { this.gatewayId = gatewayId; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getMessageBody() { return messageBody; }
    public void setMessageBody(String messageBody) { this.messageBody = messageBody; }

    public SessionConfigVO getSessionConfigVO() { return sessionConfigVO; }
    public void setSessionConfigVO(SessionConfigVO sessionConfigVO) { this.sessionConfigVO = sessionConfigVO; }

    public McpSchemaVO.JSONRPCResponse getInitializeResponse() { return initializeResponse; }
    public void setInitializeResponse(McpSchemaVO.JSONRPCResponse initializeResponse) { this.initializeResponse = initializeResponse; }

}
