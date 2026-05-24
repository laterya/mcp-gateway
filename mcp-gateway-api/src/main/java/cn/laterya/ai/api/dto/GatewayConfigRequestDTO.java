package cn.laterya.ai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 网关配置请求 DTO —— 管理后台 form 到 Controller 的数据传输
 */
public class GatewayConfigRequestDTO {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GatewayConfig {
        private String gatewayId;
        private String gatewayName;
        private String gatewayDesc;
        private String version;
        private Integer auth;
        private Integer status;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GatewayToolConfig {
        private String gatewayId;
        private Long toolId;
        private String toolName;
        private String toolType;
        private String toolDescription;
        private String toolVersion;
        private Long protocolId;
        private String protocolType;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GatewayProtocol {
        private List<HTTPProtocol> httpProtocols;

        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class HTTPProtocol {
            private Long protocolId;
            private String httpUrl;
            private String httpHeaders;
            private String httpMethod;
            private Integer timeout;
            private List<ProtocolMapping> mappings;
        }

        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class ProtocolMapping {
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

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GatewayAuth {
        private String gatewayId;
        private Integer rateLimit;
        private LocalDateTime expireTime;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GatewayProtocolImport {
        /** OpenAPI JSON 原文 */
        private String openApiJson;
        /** 需解析的接口端点列表 */
        private List<String> endpoints;
    }

}
