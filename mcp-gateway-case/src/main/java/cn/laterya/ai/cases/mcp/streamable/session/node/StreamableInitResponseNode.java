package cn.laterya.ai.cases.mcp.streamable.session.node;

import cn.laterya.ai.cases.mcp.chain.AbstractChainNode;
import cn.laterya.ai.cases.mcp.chain.SessionChainContext;
import cn.laterya.ai.domain.session.model.McpSchemaVO;
import cn.laterya.ai.domain.session.service.ISessionMessageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Streamable HTTP — 终端节点，处理 initialize 请求并将响应存入上下文
 */
@Slf4j
@Component("streamableInitResponseNode")
public class StreamableInitResponseNode extends AbstractChainNode<SessionChainContext, Void> {

    @Resource
    private ISessionMessageService sessionMessageService;

    @Override
    protected Void doHandle(SessionChainContext context) {
        log.info("Streamable HTTP 处理 initialize 请求 gatewayId:{}", context.getGatewayId());

        McpSchemaVO.JSONRPCMessage message = McpSchemaVO.deserializeJsonRpcMessage(context.getMessageBody());
        McpSchemaVO.JSONRPCResponse response = sessionMessageService.processHandlerMessage(context.getGatewayId(), message);

        context.setInitializeResponse(response);
        return null;
    }

}
