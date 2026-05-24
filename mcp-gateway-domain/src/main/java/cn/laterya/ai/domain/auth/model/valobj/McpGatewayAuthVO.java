package cn.laterya.ai.domain.auth.model.valobj;

import cn.laterya.ai.domain.auth.model.valobj.enums.AuthStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class McpGatewayAuthVO {

    private String gatewayId;
    private String apiKey;
    private Integer rateLimit;
    private LocalDateTime expireTime;
    private AuthStatusEnum.AuthConfig status;

}
