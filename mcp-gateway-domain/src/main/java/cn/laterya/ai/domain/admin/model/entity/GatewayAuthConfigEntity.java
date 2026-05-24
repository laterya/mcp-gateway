package cn.laterya.ai.domain.admin.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class GatewayAuthConfigEntity {
    private String gatewayId;
    private String apiKey;
    private Integer rateLimit;
    private LocalDateTime expireTime;
}
