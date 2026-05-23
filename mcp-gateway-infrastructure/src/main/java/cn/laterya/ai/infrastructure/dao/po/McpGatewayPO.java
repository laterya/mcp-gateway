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
public class McpGatewayPO {

    private Long id;
    private String gatewayId;
    private String gatewayName;
    private String gatewayDesc;
    private String version;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
