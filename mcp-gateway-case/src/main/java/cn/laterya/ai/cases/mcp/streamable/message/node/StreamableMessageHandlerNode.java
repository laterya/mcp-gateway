package cn.laterya.ai.cases.mcp.streamable.message.node;

import cn.laterya.ai.cases.mcp.chain.MessageChainContext;
import cn.laterya.ai.cases.mcp.streamable.message.AbstractStreamableMessageChainNode;
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
 * Streamable HTTP — 消息处理终端节点（处理消息 + Sink 推送）
 */
@Slf4j
@Component("streamableMessageHandlerNode")
public class StreamableMessageHandlerNode extends AbstractStreamableMessageChainNode {

    @Resource
    private ISessionMessageService sessionMessageService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected ResponseEntity<Void> doHandle(HandleMessageCommandEntity command, MessageChainContext context) {
        try {
            log.info("Streamable HTTP 消息处理 MessageHandlerNode gatewayId:{}", command.getGatewayId());

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
            log.error("Streamable HTTP 消息处理失败 gatewayId:{} sessionId:{}", command.getGatewayId(), command.getSessionId(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

}
