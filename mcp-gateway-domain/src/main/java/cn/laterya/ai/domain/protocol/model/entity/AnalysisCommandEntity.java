package cn.laterya.ai.domain.protocol.model.entity;

import cn.laterya.ai.domain.protocol.model.valobj.enums.AnalysisTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 协议解析命令实体 —— 封装一次解析请求所需的全部输入
 *
 * <p>type 预留多协议扩展点（当前仅 swagger），后续加 RPC/GraphQL 解析时扩展枚举值即可
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisCommandEntity {

    /** 解析类型枚举（当前仅 swagger，预留 rpc 等扩展） */
    private AnalysisTypeEnum type;

    /** OpenAPI 标准 JSON 字符串（Swagger /v3/api-docs 导出的原始数据） */
    private String openApiJson;

    /** 需解析的接口端点列表（如 /api/v1/mcp/get_company_employee） */
    private List<String> endpoints;

}
