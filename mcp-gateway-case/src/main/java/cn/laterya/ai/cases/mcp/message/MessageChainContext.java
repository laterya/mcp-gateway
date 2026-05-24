package cn.laterya.ai.cases.mcp.message;

import cn.laterya.ai.domain.session.model.SessionConfigVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息编排链上下文 —— 在各节点间传递的可变状态
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageChainContext {

    private SessionConfigVO sessionConfigVO;

}
