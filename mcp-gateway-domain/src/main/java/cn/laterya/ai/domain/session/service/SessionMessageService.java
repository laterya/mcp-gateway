package cn.laterya.ai.domain.session.service;

import cn.laterya.ai.domain.session.model.McpSchemaVO;
import cn.laterya.ai.domain.session.model.enums.SessionMessageHandlerMethodEnum;
import cn.laterya.ai.domain.session.service.message.IRequestHandler;
import cn.laterya.ai.types.enums.ResponseCode;
import cn.laterya.ai.types.exception.AppException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 会话消息处理服务
 *
 * <p>消息三路分发：
 * <ul>
 *   <li>JSONRPCRequest → 枚举策略路由到对应 Handler 处理</li>
 *   <li>JSONRPCResponse → 仅日志（MCP 场景下服务端一般不收到 Response）</li>
 *   <li>JSONRPCNotification → 仅日志（如 notifications/initialized）</li>
 * </ul>
 *
 * <p>策略路由链路：
 * <pre>
 * request.method
 *   → SessionMessageHandlerMethodEnum.getByMethod() 获取 Bean 名
 *   → requestHandlerMap.get(handlerName) 获取处理器实例
 *   → handler.handle(request) 执行并返回响应
 * </pre>
 */
@Slf4j
@Service
public class SessionMessageService implements ISessionMessageService {

    @Resource
    private Map<String, IRequestHandler> requestHandlerMap;

    @Override
    public McpSchemaVO.JSONRPCResponse processHandlerMessage(String gatewayId, McpSchemaVO.JSONRPCMessage message) {

        if (message instanceof McpSchemaVO.JSONRPCResponse response) {
            log.info("收到结果消息");
            return null;
        }

        if (message instanceof McpSchemaVO.JSONRPCRequest request) {
            String method = request.method();
            log.info("开始处理请求 method:{}", method);

            SessionMessageHandlerMethodEnum methodEnum = SessionMessageHandlerMethodEnum.getByMethod(method);
            if (null == methodEnum) {
                throw new AppException(ResponseCode.METHOD_NOT_FOUND.getCode(), ResponseCode.METHOD_NOT_FOUND.getInfo());
            }

            String handlerName = methodEnum.getHandlerName();
            IRequestHandler handler = requestHandlerMap.get(handlerName);
            if (null == handler) {
                throw new AppException(ResponseCode.METHOD_NOT_FOUND.getCode(), ResponseCode.METHOD_NOT_FOUND.getInfo());
            }

            return handler.handle(gatewayId, request);
        }

        if (message instanceof McpSchemaVO.JSONRPCNotification notification) {
            log.info("收到通知 method:{} params:{}", notification.method(), notification.params());
            return null;
        }

        return null;
    }

}
