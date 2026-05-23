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
public class McpProtocolMappingPO {

    private Long id;
    private Long protocolId;
    private String mappingType;
    private String parentPath;
    private String fieldName;
    private String mcpPath;
    private String mcpType;
    private String mcpDesc;
    private Integer isRequired;
    private Integer sortOrder;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
