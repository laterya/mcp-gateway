package cn.laterya.ai.infrastructure.dao.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class McpGatewayAuthPO {

    private Long id;
    private String gatewayId;
    private String apiKey;
    private Integer rateLimit;
    private LocalDateTime expireTime;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
