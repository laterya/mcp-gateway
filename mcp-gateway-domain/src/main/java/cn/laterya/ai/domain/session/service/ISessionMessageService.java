package cn.laterya.ai.domain.session.service;

import cn.laterya.ai.domain.session.model.McpSchemaVO;

/**
 * 会话消息处理服务接口（领域 Port）
 *
 * <p>职责：接收 JSON-RPC 请求，根据 method 分发到对应的策略处理器。
 */
public interface ISessionMessageService {

    /**
     * 处理会话消息
     *
     * @param request JSON-RPC 请求
     * @return JSON-RPC 响应
     */
    McpSchemaVO.JSONRPCResponse processHandlerMessage(McpSchemaVO.JSONRPCRequest request);

}
