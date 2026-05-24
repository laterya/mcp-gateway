package cn.laterya.ai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class GatewayToolConfigDTO implements Serializable {
    private String gatewayId;
    private Long toolId;
    private String toolName;
    private String toolType;
    private String toolDescription;
    private String toolVersion;
    private Long protocolId;
    private String protocolType;
}
