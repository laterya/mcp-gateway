package cn.laterya.ai.domain.gateway.model.entity;

import cn.laterya.ai.domain.gateway.model.valobj.GatewayConfigVO;
import cn.laterya.ai.types.enums.GatewayEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网关配置命令实体
 *
 * <p>充血设计：{@link #buildUpdateGatewayAuthStatusVO} 工厂方法针对"只更新鉴权状态"
 * 这种子场景，自动构造最小化的 VO，避免调用方手写 builder 链填充无关字段。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayConfigCommandEntity {

    private GatewayConfigVO gatewayConfigVO;

    /** 快速构造"仅更新鉴权状态"的命令实体 */
    public static GatewayConfigCommandEntity buildUpdateGatewayAuthStatusVO(
            String gatewayId, GatewayEnum.GatewayAuthStatusEnum auth) {
        return GatewayConfigCommandEntity.builder()
                .gatewayConfigVO(GatewayConfigVO.builder()
                        .gatewayId(gatewayId)
                        .auth(auth)
                        .build())
                .build();
    }

}
