package cn.laterya.ai.domain.session.model.entity;

import cn.laterya.ai.domain.session.model.McpSchemaVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息处理命令实体
 *
 * <p>封装 handleMessage 接口的入参（gatewayId、sessionId、messageBody），
 * 便捷构造器内聚 JSON-RPC 反序列化，避免调用方重复处理。
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HandleMessageCommandEntity {

    private String gatewayId;
    private String sessionId;
    private McpSchemaVO.JSONRPCMessage jsonrpcMessage;

    public HandleMessageCommandEntity(String gatewayId, String sessionId, String messageBody) {
        this.gatewayId = gatewayId;
        this.sessionId = sessionId;
        this.jsonrpcMessage = McpSchemaVO.deserializeJsonRpcMessage(messageBody);
    }

}
