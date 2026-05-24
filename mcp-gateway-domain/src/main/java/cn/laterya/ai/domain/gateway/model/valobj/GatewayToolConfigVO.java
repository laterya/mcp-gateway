package cn.laterya.ai.domain.gateway.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayToolConfigVO {

    private String gatewayId;
    private Long toolId;
    private String toolName;
    private String toolType;
    private String toolDescription;
    private String toolVersion;
    /** 关联的协议ID（mcp_protocol_http.protocol_id） */
    private Long protocolId;
    /** 协议类型：http/dubbo/rabbitmq */
    private String protocolType;

}
