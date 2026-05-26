package cn.laterya.ai.cases.mcp.chain.message;

import cn.laterya.ai.cases.mcp.chain.AbstractChainNode;
import cn.laterya.ai.cases.mcp.chain.MessageChainContext;
import cn.laterya.ai.domain.session.model.McpSchemaVO;
import cn.laterya.ai.domain.session.service.ISessionMessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;

/**
 * 共享消息链终端节点 — 处理消息 + Sink 推送
 */
@Slf4j
@Component("mcpMessageHandlerNode")
public class MessageHandlerNode extends AbstractChainNode<MessageChainContext, ResponseEntity<Void>> {

    @Resource
    private ISessionMessageService sessionMessageService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected ResponseEntity<Void> doHandle(MessageChainContext context) {
        var command = context.getCommand();
        try {
            log.info("消息处理 MessageHandlerNode gatewayId:{}", command.getGatewayId());

            McpSchemaVO.JSONRPCResponse jsonrpcResponse =
                    sessionMessageService.processHandlerMessage(command.getGatewayId(), command.getJsonrpcMessage());

            if (null != jsonrpcResponse) {
                String responseJson = objectMapper.writeValueAsString(jsonrpcResponse);
                var sessionConfigVO = context.getSessionConfigVO();
                sessionConfigVO.getSink().tryEmitNext(ServerSentEvent.<String>builder()
                        .event("message")
                        .data(responseJson)
                        .build());
            }

            return ResponseEntity.accepted().build();
        } catch (Exception e) {
            log.error("消息处理失败 gatewayId:{} sessionId:{}", command.getGatewayId(), command.getSessionId(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

}
