package cn.laterya.ai.domain.session.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 工具协议配置值对象
 *
 * <p>一个工具绑定一种协议（当前为 HTTP），协议下有多个字段映射（request 类型）。
 * 由 mcp_protocol_http + mcp_protocol_mapping 组装。
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class McpToolProtocolConfigVO {

    private HTTPConfig httpConfig;
    private List<ProtocolMapping> requestProtocolMappings;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HTTPConfig {
        private String httpUrl;
        private String httpMethod;
        private String httpHeaders;
        private Integer timeout;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProtocolMapping {
        private String parentPath;
        private String fieldName;
        private String mcpPath;
        private String mcpType;
        private String mcpDesc;
        private Integer isRequired;
        private Integer sortOrder;
    }

}
