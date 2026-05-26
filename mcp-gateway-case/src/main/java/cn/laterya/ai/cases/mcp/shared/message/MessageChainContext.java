package cn.laterya.ai.cases.mcp.shared.message;

import cn.laterya.ai.domain.session.model.SessionConfigVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息编排链上下文（SSE / Streamable 共用）
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageChainContext {

    private SessionConfigVO sessionConfigVO;

}
