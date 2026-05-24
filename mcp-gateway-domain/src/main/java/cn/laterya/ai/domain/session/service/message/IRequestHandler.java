package cn.laterya.ai.domain.session.service.message;

import cn.laterya.ai.domain.session.model.McpSchemaVO;

/**
 * MCP 消息处理器策略接口
 *
 * <p>每种 JSON-RPC method 对应一个实现类，
 * 通过 Spring Map 注入 + 枚举路由实现策略分发。
 */
public interface IRequestHandler {

    /**
     * 处理 JSON-RPC 请求
     *
     * @param gatewayId 网关标识
     * @param message   请求消息
     * @return 响应消息
     */
    McpSchemaVO.JSONRPCResponse handle(String gatewayId, McpSchemaVO.JSONRPCRequest message);

}
