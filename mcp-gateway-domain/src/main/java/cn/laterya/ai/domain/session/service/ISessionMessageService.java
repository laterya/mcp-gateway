package cn.laterya.ai.domain.session.service;

import cn.laterya.ai.domain.session.model.McpSchemaVO;

/**
 * 会话消息处理服务接口（领域 Port）
 *
 * <p>职责：接收 JSON-RPC 消息，根据类型（Request / Notification / Response）分发处理。
 * Request 走策略路由，Notification/Response 仅日志。
 */
public interface ISessionMessageService {

    /**
     * 处理会话消息
     *
     * @param gatewayId 网关标识
     * @param message   JSON-RPC 消息（Request / Notification / Response）
     * @return JSON-RPC 响应，Notification/Response 返回 null
     */
    McpSchemaVO.JSONRPCResponse processHandlerMessage(String gatewayId, McpSchemaVO.JSONRPCMessage message);

}
