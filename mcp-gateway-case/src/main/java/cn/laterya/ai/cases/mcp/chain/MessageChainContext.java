package cn.laterya.ai.cases.mcp.chain;

import cn.laterya.ai.domain.session.model.SessionConfigVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息编排链统一上下文（SSE + Streamable HTTP 共用）
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageChainContext {

    private SessionConfigVO sessionConfigVO;

}
