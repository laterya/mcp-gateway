package cn.laterya.ai.domain.admin.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class GatewayProtocolQueryEntity {
    private Long protocolId;
    private String httpUrl;
    private Integer page;
    private Integer rows;
}
