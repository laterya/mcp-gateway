package cn.laterya.ai.cases.mcp.chain;

import cn.laterya.ai.domain.session.model.SessionConfigVO;
import cn.laterya.ai.domain.session.model.entity.HandleMessageCommandEntity;

/**
 * 统一消息编排链上下文
 *
 * <p>SSE 和 Streamable HTTP 消息链共用。command 字段贯穿整条链路，sessionConfigVO 由 SessionNode 填充。
 */
public class MessageChainContext {

    private HandleMessageCommandEntity command;
    private SessionConfigVO sessionConfigVO;

    public MessageChainContext() {}

    public MessageChainContext(HandleMessageCommandEntity command) {
        this.command = command;
    }

    public HandleMessageCommandEntity getCommand() { return command; }
    public void setCommand(HandleMessageCommandEntity command) { this.command = command; }

    public SessionConfigVO getSessionConfigVO() { return sessionConfigVO; }
    public void setSessionConfigVO(SessionConfigVO sessionConfigVO) { this.sessionConfigVO = sessionConfigVO; }

}
