package cn.laterya.ai.cases.mcp.sse.chain;

import cn.laterya.ai.domain.session.model.SessionConfigVO;

/**
 * SSE 传输 — 会话编排链上下文
 */
public class SessionChainContext {

    private String apiKey;
    private SessionConfigVO sessionConfigVO;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public SessionConfigVO getSessionConfigVO() {
        return sessionConfigVO;
    }

    public void setSessionConfigVO(SessionConfigVO sessionConfigVO) {
        this.sessionConfigVO = sessionConfigVO;
    }

}
