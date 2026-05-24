package cn.laterya.ai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class GatewayProtocolDTO implements Serializable {
    private Long protocolId;
    private String httpUrl;
    private String httpMethod;
    private String httpHeaders;
    private Integer timeout;
    private List<ProtocolMappingDTO> mappings;

    @Data @Builder @AllArgsConstructor @NoArgsConstructor
    public static class ProtocolMappingDTO implements Serializable {
        private String mappingType;
        private String parentPath;
        private String fieldName;
        private String mcpPath;
        private String mcpType;
        private String mcpDesc;
        private Integer isRequired;
        private Integer sortOrder;
    }
}
