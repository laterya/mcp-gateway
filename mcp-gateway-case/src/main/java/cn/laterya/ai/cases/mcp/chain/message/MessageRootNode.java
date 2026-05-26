package cn.laterya.ai.cases.mcp.chain.message;

import cn.laterya.ai.cases.mcp.chain.AbstractChainNode;
import cn.laterya.ai.cases.mcp.chain.MessageChainContext;
import cn.laterya.ai.domain.auth.model.entity.RateLimitCommandEntity;
import cn.laterya.ai.domain.auth.service.IAuthRateLimitService;
import cn.laterya.ai.domain.session.model.McpSchemaVO;
import cn.laterya.ai.domain.session.model.enums.SessionMessageHandlerMethodEnum;
import cn.laterya.ai.types.enums.McpErrorCodes;
import cn.laterya.ai.types.exception.AppException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * 共享消息链根节点 — 限流 + 异常兜底
 */
@Slf4j
@Component("mcpMessageRootNode")
public class MessageRootNode extends AbstractChainNode<MessageChainContext, ResponseEntity<Void>> {

    @Resource
    private IAuthRateLimitService authRateLimitService;

    @Override
    protected ResponseEntity<Void> doHandle(MessageChainContext context) {
        var command = context.getCommand();
        try {
            log.info("消息处理 RootNode gatewayId:{} sessionId:{}", command.getGatewayId(), command.getSessionId());

            if (command.getJsonrpcMessage() instanceof McpSchemaVO.JSONRPCRequest request) {
                SessionMessageHandlerMethodEnum methodEnum = SessionMessageHandlerMethodEnum.getByMethod(request.method());
                if (SessionMessageHandlerMethodEnum.TOOLS_CALL.equals(methodEnum)) {
                    boolean isHit = authRateLimitService.rateLimit(new RateLimitCommandEntity(command.getGatewayId(), command.getApiKey()));
                    if (isHit) {
                        log.warn("消息处理命中限流 gatewayId:{} apiKey:{}", command.getGatewayId(), command.getApiKey());
                        throw new AppException(McpErrorCodes.INSUFFICIENT_PERMISSIONS, "fail to auth apikey rateLimiter");
                    }
                }
            }

            return fireNext(context);
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("消息处理异常 gatewayId:{} sessionId:{}", command.getGatewayId(), command.getSessionId(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

}
