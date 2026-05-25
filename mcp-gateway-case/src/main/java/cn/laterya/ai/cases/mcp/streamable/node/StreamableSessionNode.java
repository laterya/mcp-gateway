package cn.laterya.ai.cases.mcp.streamable.node;

import cn.laterya.ai.cases.mcp.streamable.AbstractStreamableChainNode;
import cn.laterya.ai.cases.mcp.streamable.StreamableChainContext;
import cn.laterya.ai.domain.session.model.McpSchemaVO;
import cn.laterya.ai.domain.session.model.SessionConfigVO;
import cn.laterya.ai.domain.session.model.enums.SessionMessageHandlerMethodEnum;
import cn.laterya.ai.domain.session.service.ISessionManagementService;
import cn.laterya.ai.types.enums.McpErrorCodes;
import cn.laterya.ai.types.exception.AppException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Streamable HTTP 会话节点 —— 解析消息，initialize 时创建 session，非 initialize 时查找已有 session
 */
@Slf4j
@Component
public class StreamableSessionNode extends AbstractStreamableChainNode {

    @Resource
    private ISessionManagementService sessionManagementService;

    @Override
    protected void doHandle(String gatewayId, StreamableChainContext context) {
        // 解析 JSON-RPC 消息
        McpSchemaVO.JSONRPCMessage message = McpSchemaVO.deserializeJsonRpcMessage(context.getMessageBody());
        context.setJsonRpcMessage(message);

        if (message instanceof McpSchemaVO.JSONRPCRequest request) {
            if (SessionMessageHandlerMethodEnum.INITIALIZE.getMethod().equals(request.method())) {
                // initialize 请求：创建新 session
                SessionConfigVO session = sessionManagementService.createStreamableSession(gatewayId);
                context.setSessionConfigVO(session);
                context.setSessionId(session.getSessionId());
                log.info("Streamable HTTP 创建会话 gatewayId:{} sessionId:{}", gatewayId, session.getSessionId());
            } else {
                // 非 initialize：从 Mcp-Session-Id 头找到已有 session
                if (context.getSessionId() != null) {
                    SessionConfigVO session = sessionManagementService.getSession(context.getSessionId());
                    if (session == null) {
                        throw new AppException(McpErrorCodes.SESSION_NOT_FOUND, "session not found or expired");
                    }
                    context.setSessionConfigVO(session);
                }
            }
        }

        fireNext(gatewayId, context);
    }

}
