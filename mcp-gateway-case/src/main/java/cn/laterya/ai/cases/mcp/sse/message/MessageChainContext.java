package cn.laterya.ai.cases.mcp.sse.message;

import cn.laterya.ai.domain.session.model.SessionConfigVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SSE 传输 — 消息编排链上下文
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageChainContext {

    private SessionConfigVO sessionConfigVO;

}
