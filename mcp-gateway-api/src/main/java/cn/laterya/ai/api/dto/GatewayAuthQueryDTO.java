package cn.laterya.ai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class GatewayAuthQueryDTO {
    private String gatewayId;
    private String apiKey;
    private Integer page;
    private Integer rows;
}
