package cn.laterya.ai.cases.mcp.streamable.message.node;

import cn.laterya.ai.cases.mcp.streamable.message.AbstractStreamableMessageChainNode;
import cn.laterya.ai.cases.mcp.streamable.message.StreamableMessageChainContext;
import cn.laterya.ai.domain.auth.model.entity.RateLimitCommandEntity;
import cn.laterya.ai.domain.auth.service.IAuthRateLimitService;
import cn.laterya.ai.domain.session.model.McpSchemaVO;
import cn.laterya.ai.domain.session.model.entity.HandleMessageCommandEntity;
import cn.laterya.ai.domain.session.model.enums.SessionMessageHandlerMethodEnum;
import cn.laterya.ai.types.enums.McpErrorCodes;
import cn.laterya.ai.types.exception.AppException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Streamable HTTP — 消息处理根节点（限流 + 异常兜底）
 */
@Slf4j
@Component("streamableMessageRootNode")
public class StreamableMessageRootNode extends AbstractStreamableMessageChainNode {

    @Resource
    private IAuthRateLimitService authRateLimitService;

    @Override
    protected ResponseEntity<Void> doHandle(HandleMessageCommandEntity command, StreamableMessageChainContext context) {
        try {
            log.info("Streamable HTTP 消息处理 RootNode gatewayId:{} sessionId:{}", command.getGatewayId(), command.getSessionId());

            if (command.getJsonrpcMessage() instanceof McpSchemaVO.JSONRPCRequest request) {
                SessionMessageHandlerMethodEnum methodEnum = SessionMessageHandlerMethodEnum.getByMethod(request.method());
                if (SessionMessageHandlerMethodEnum.TOOLS_CALL.equals(methodEnum)) {
                    boolean isHit = authRateLimitService.rateLimit(new RateLimitCommandEntity(command.getGatewayId(), command.getApiKey()));
                    if (isHit) {
                        log.warn("Streamable HTTP 消息处理命中限流 gatewayId:{} apiKey:{}", command.getGatewayId(), command.getApiKey());
                        throw new AppException(McpErrorCodes.INSUFFICIENT_PERMISSIONS, "fail to auth apikey rateLimiter");
                    }
                }
            }

            return fireNext(command, context);
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Streamable HTTP 消息处理异常 gatewayId:{} sessionId:{}", command.getGatewayId(), command.getSessionId(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

}
