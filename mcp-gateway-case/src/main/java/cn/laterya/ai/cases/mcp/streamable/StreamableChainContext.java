package cn.laterya.ai.cases.mcp.streamable;

import cn.laterya.ai.domain.session.model.McpSchemaVO;
import cn.laterya.ai.domain.session.model.SessionConfigVO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Streamable HTTP 编排链上下文
 *
 * <p>在 Streamable HTTP 链节点间传递的可变状态。
 * 与 SSE 链的 SessionChainContext 类似但字段不同：
 * - 无需 sink 相关字段
 * - 增加 messageBody 和 jsonRpcMessage 用于消息处理
 */
@Data
@Accessors(chain = true)
public class StreamableChainContext {

    private String apiKey;
    private String messageBody;
    private McpSchemaVO.JSONRPCMessage jsonRpcMessage;
    private SessionConfigVO sessionConfigVO;
    private McpSchemaVO.JSONRPCResponse response;
    private String sessionId;

}
