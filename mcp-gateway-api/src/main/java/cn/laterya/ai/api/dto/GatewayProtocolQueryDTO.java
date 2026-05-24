package cn.laterya.ai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class GatewayProtocolQueryDTO {
    private Long protocolId;
    private String httpUrl;
    private Integer page;
    private Integer rows;
}
