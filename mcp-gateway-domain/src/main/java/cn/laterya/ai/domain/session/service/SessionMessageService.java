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
 * <p>策略分发核心：
 * <pre>
 * JSON-RPC request.method
 *   → SessionMessageHandlerMethodEnum.getByMethod() 获取 Bean 名
 *   → requestHandlerMap.get(handlerName) 获取处理器实例
 *   → handler.handle(request) 执行并返回响应
 * </pre>
 *
 * <p>Spring Map 注入机制：
 * {@code Map<String, IRequestHandler>} 会自动注入所有 IRequestHandler 实现，
 * key 为 Bean 名称（即 @Service("xxxHandler") 指定的值）。
 */
@Slf4j
@Service
public class SessionMessageService implements ISessionMessageService {

    /** 策略注入：所有 IRequestHandler 实现以 Bean 名为 key 自动注入 */
    @Resource
    private Map<String, IRequestHandler> requestHandlerMap;

    @Override
    public McpSchemaVO.JSONRPCResponse processHandlerMessage(McpSchemaVO.JSONRPCRequest request) {
        String method = request.method();
        log.info("开始处理请求 method:{}", method);

        // 枚举路由：method 字符串 → handler Bean 名
        SessionMessageHandlerMethodEnum methodEnum = SessionMessageHandlerMethodEnum.getByMethod(method);
        if (null == methodEnum) {
            throw new AppException(ResponseCode.METHOD_NOT_FOUND.getCode(), ResponseCode.METHOD_NOT_FOUND.getInfo());
        }

        String handlerName = methodEnum.getHandlerName();
        IRequestHandler handler = requestHandlerMap.get(handlerName);
        if (null == handler) {
            throw new AppException(ResponseCode.METHOD_NOT_FOUND.getCode(), ResponseCode.METHOD_NOT_FOUND.getInfo());
        }

        return handler.handle(request);
    }

}
