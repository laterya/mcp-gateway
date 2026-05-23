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
public class McpProtocolHttpPO {

    private Long id;
    private Long protocolId;
    private String httpUrl;
    private String httpMethod;
    private String httpHeaders;
    private Integer timeout;
    private Integer retryTimes;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
