package cn.laterya.ai.cases.mcp.sse.message.node;

import cn.laterya.ai.cases.mcp.sse.message.AbstractMessageChainNode;
import cn.laterya.ai.cases.mcp.sse.message.MessageChainContext;
import cn.laterya.ai.domain.session.model.McpSchemaVO;
import cn.laterya.ai.domain.session.model.SessionConfigVO;
import cn.laterya.ai.domain.session.model.entity.HandleMessageCommandEntity;
import cn.laterya.ai.domain.session.service.ISessionMessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;

/**
 * SSE 传输 — 消息处理终端节点（处理消息 + Sink 推送）
 */
@Slf4j
@Component("sseMessageHandlerNode")
public class MessageHandlerNode extends AbstractMessageChainNode {

    @Resource
    private ISessionMessageService sessionMessageService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected ResponseEntity<Void> doHandle(HandleMessageCommandEntity command, MessageChainContext context) {
        try {
            log.info("消息处理 mcp message MessageHandlerNode gatewayId:{}", command.getGatewayId());

            McpSchemaVO.JSONRPCResponse jsonrpcResponse =
                    sessionMessageService.processHandlerMessage(command.getGatewayId(), command.getJsonrpcMessage());

            if (null != jsonrpcResponse) {
                String responseJson = objectMapper.writeValueAsString(jsonrpcResponse);
                SessionConfigVO sessionConfigVO = context.getSessionConfigVO();
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
