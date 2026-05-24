package cn.laterya.ai.cases.mcp.chain;

import cn.laterya.ai.domain.session.model.SessionConfigVO;

/**
 * 会话编排链上下文 —— 在各节点间传递的可变状态
 *
 * <p>类比：流水线上的零件托盘，每个工位（节点）往托盘上放/取零件，
 * 下一个工位接着处理。这样节点之间不直接引用，通过上下文解耦。
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
