package cn.laterya.ai.cases.mcp.streamable.message;

import cn.laterya.ai.domain.session.model.SessionConfigVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Streamable HTTP — 消息编排链上下文
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StreamableMessageChainContext {

    private SessionConfigVO sessionConfigVO;

}
