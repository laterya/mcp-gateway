package cn.laterya.ai.cases.mcp.streamable.node;

import cn.laterya.ai.cases.mcp.streamable.AbstractStreamableChainNode;
import cn.laterya.ai.cases.mcp.streamable.StreamableChainContext;
import cn.laterya.ai.domain.session.model.McpSchemaVO;
import cn.laterya.ai.domain.session.service.ISessionMessageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Streamable HTTP 处理节点 —— 调用 domain 层消息路由，结果写入上下文
 *
 * <p>终端节点，不调用 fireNext()。
 */
@Slf4j
@Component
public class StreamableHandlerNode extends AbstractStreamableChainNode {

    @Resource
    private ISessionMessageService sessionMessageService;

    @Override
    protected void doHandle(String gatewayId, StreamableChainContext context) {
        McpSchemaVO.JSONRPCMessage message = context.getJsonRpcMessage();
        McpSchemaVO.JSONRPCResponse response = sessionMessageService.processHandlerMessage(gatewayId, message);
        context.setResponse(response);
        log.info("Streamable HTTP 消息处理完成 gatewayId:{}", gatewayId);
    }

}
