package cn.laterya.ai.domain.gateway.model.valobj;

import cn.laterya.ai.types.enums.GatewayEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GatewayConfigVO {

    private String gatewayId;
    private String gatewayName;
    private String gatewayDesc;
    private String version;
    /** 鉴权模式：ENABLE(1)/DISABLE(0) */
    private GatewayEnum.GatewayAuthStatusEnum auth;
    /** 网关状态：NOT_VERIFIED(0)/STRONG_VERIFIED(1) */
    private GatewayEnum.GatewayStatus status;

}
