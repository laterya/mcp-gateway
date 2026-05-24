package cn.laterya.ai.domain.protocol.service.analysis.strategy.impl;

import cn.laterya.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;
import cn.laterya.ai.domain.protocol.service.analysis.strategy.AbstractProtocolAnalysisStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 解析 requestBody（对象入参）
 *
 * <p>处理 OpenAPI 中 POST 接口的请求体对象，从 $ref 引用展开 schema，
 * 将对象名作为根节点映射，再递归解析其内部属性。
 */
@Slf4j
@Component("requestBodyAnalysis")
@Order(1)
public class RequestBodyAnalysisStrategy extends AbstractProtocolAnalysisStrategy {

    @Override
    public void doAnalysis(JsonNode operation, JsonNode schemas, List<HTTPProtocolVO.ProtocolMapping> mappings) {
        JsonNode requestBody = operation.path("requestBody");
        if (requestBody.isMissingNode()) return;

        JsonNode appJson = requestBody.path("content").path("application/json");
        if (appJson.isMissingNode()) return;

        String ref = appJson.path("schema").path("$ref").asText(null);
        if (ref == null) return;

        String refName = ref.substring(ref.lastIndexOf('/') + 1);
        JsonNode reqSchema = schemas.path(refName);
        // 首字母小写作为 MCP 根对象名（如 XxxRequest01 → xxxRequest01）
        String rootName = toLowerCamel(refName);

        mappings.add(HTTPProtocolVO.ProtocolMapping.builder()
                .mappingType("request")
                .parentPath(null)
                .fieldName(rootName)
                .mcpPath(rootName)
                .mcpType(convertType(reqSchema.path("type").asText(null)))
                .mcpDesc(reqSchema.path("description").asText(null))
                .isRequired(1)
                .sortOrder(1)
                .build());

        parseProperties(rootName, reqSchema.path("properties"), reqSchema.path("required"), schemas, mappings);
    }

}
