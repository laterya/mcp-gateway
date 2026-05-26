package cn.laterya.ai.cases.mcp.streamable.session.node;

import cn.laterya.ai.cases.mcp.chain.SessionChainContext;
import cn.laterya.ai.cases.mcp.streamable.session.AbstractStreamableSessionChainNode;
import cn.laterya.ai.domain.session.model.McpSchemaVO;
import cn.laterya.ai.domain.session.service.ISessionMessageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Streamable HTTP — 终端节点，处理 initialize 请求并将响应存入上下文
 *
 * <p>与 SSE 的 SseResponseNode 的关键区别：
 * SSE 终端节点返回 Flux<ServerSentEvent<String>>（SSE 流），
 * 而这里把 JSON-RPC 响应存入 context，由 Controller 构建 HTTP 响应（body + Mcp-Session-Id header）。
 */
@Slf4j
@Component("streamableInitResponseNode")
public class StreamableInitResponseNode extends AbstractStreamableSessionChainNode {

    @Resource
    private ISessionMessageService sessionMessageService;

    @Override
    protected Void doHandle(String gatewayId, SessionChainContext context) {
        log.info("Streamable HTTP 处理 initialize 请求 gatewayId:{}", gatewayId);

        // 解析消息并路由到 InitializeHandler
        McpSchemaVO.JSONRPCMessage message = McpSchemaVO.deserializeJsonRpcMessage(context.getMessageBody());
        McpSchemaVO.JSONRPCResponse response = sessionMessageService.processHandlerMessage(gatewayId, message);

        context.setInitializeResponse(response);
        // 终端节点，不调用 fireNext
        return null;
    }

}
