package cn.laterya.ai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 网关配置列表项 DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GatewayConfigDTO implements Serializable {

    private String gatewayId;
    private String gatewayName;
    private String gatewayDesc;
    private String version;
    private Integer auth;
    private Integer status;

}
