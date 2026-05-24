package cn.laterya.ai.domain.gateway.model.entity;

import cn.laterya.ai.domain.gateway.model.valobj.GatewayToolConfigVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网关工具配置命令实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayToolConfigCommandEntity {

    private GatewayToolConfigVO gatewayToolConfigVO;

    /** 快速构造"仅更新工具关联协议"的命令实体 */
    public static GatewayToolConfigCommandEntity buildUpdateGatewayProtocol(
            String gatewayId, Long toolId, Long protocolId, String protocolType) {
        return GatewayToolConfigCommandEntity.builder()
                .gatewayToolConfigVO(GatewayToolConfigVO.builder()
                        .gatewayId(gatewayId)
                        .toolId(toolId)
                        .protocolId(protocolId)
                        .protocolType(protocolType)
                        .build())
                .build();
    }

}
