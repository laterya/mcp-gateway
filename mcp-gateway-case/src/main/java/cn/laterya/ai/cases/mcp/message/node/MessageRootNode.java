package cn.laterya.ai.cases.mcp.message.node;

import cn.laterya.ai.cases.mcp.message.AbstractMessageChainNode;
import cn.laterya.ai.cases.mcp.message.MessageChainContext;
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
 * 消息处理根节点 —— 限流检查 + 日志记录 + 异常兜底
 */
@Slf4j
@Component
public class MessageRootNode extends AbstractMessageChainNode {

    @Resource
    private IAuthRateLimitService authRateLimitService;

    @Override
    protected ResponseEntity<Void> doHandle(HandleMessageCommandEntity command, MessageChainContext context) {
        try {
            log.info("消息处理 mcp message RootNode gatewayId:{} sessionId:{}", command.getGatewayId(), command.getSessionId());

            // 仅对工具调用做限流，其他方法（initialize、tools/list）不限流
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

            return fireNext(command, context);
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("消息处理异常 gatewayId:{} sessionId:{}", command.getGatewayId(), command.getSessionId(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

}
