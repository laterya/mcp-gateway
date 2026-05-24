package cn.laterya.ai.domain.protocol.model.valobj.http;

import lombok.*;

import java.util.List;

/**
 * HTTP 协议值对象 —— 对应库表 mcp_protocol_http + mcp_protocol_mapping
 *
 * <p>由 Swagger OpenAPI JSON 解析而来，包含协议基础信息及字段映射列表。
 * 本 VO 是"协议解析"和"协议落库"之间的中间表示。
 */
@Data
public class HTTPProtocolVO {

    /** 接口完整 URL（baseUrl + endpoint） */
    private String httpUrl;
    /** HTTP 请求头（JSON 格式，如 {"Content-Type": "application/json"}） */
    private String httpHeaders;
    /** HTTP 请求方法：get/post/put/delete */
    private String httpMethod;
    /** 超时时间（毫秒） */
    private Integer timeout;

    /** 请求/响应字段映射列表 */
    private List<ProtocolMapping> mappings;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProtocolMapping {
        /** 映射类型：request-请求参数映射，response-响应数据映射 */
        private String mappingType;
        /** 父级路径（根节点为 null，如 xxxRequest01 作为根对象时 parentPath=null） */
        private String parentPath;
        /** 字段名称 */
        private String fieldName;
        /** MCP 完整路径（点分隔，如 xxxRequest01.company.name） */
        private String mcpPath;
        /** MCP 数据类型：string/number/boolean/object/array */
        private String mcpType;
        /** MCP 字段描述（来自 Swagger @Schema 注解） */
        private String mcpDesc;
        /** 是否必填：0-否，1-是 */
        private Integer isRequired;
        /** 同级字段排序顺序 */
        private Integer sortOrder;
    }

}
