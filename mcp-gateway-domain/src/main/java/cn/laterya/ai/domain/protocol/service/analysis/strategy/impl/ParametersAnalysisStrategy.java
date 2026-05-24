package cn.laterya.ai.domain.protocol.service.analysis.strategy.impl;

import cn.laterya.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;
import cn.laterya.ai.domain.protocol.service.analysis.strategy.AbstractProtocolAnalysisStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 解析 parameters（属性入参）
 *
 * <p>处理 OpenAPI 中 GET 接口的 query/path 参数，逐个参数生成映射。
 * 支持 $ref 引用（参数类型为复杂对象时的递归解析）。
 */
@Slf4j
@Component("parametersAnalysis")
@Order(2)
public class ParametersAnalysisStrategy extends AbstractProtocolAnalysisStrategy {

    @Override
    public void doAnalysis(JsonNode operation, JsonNode schemas, List<HTTPProtocolVO.ProtocolMapping> mappings) {
        JsonNode parameters = operation.path("parameters");
        if (!parameters.isArray()) return;

        for (JsonNode param : parameters) {
            String in = param.path("in").asText();
            if (!"query".equals(in) && !"path".equals(in)) continue;

            String name = param.path("name").asText();
            boolean required = param.path("required").asBoolean(false);
            String description = param.path("description").asText(null);

            JsonNode schema = param.path("schema");
            String type = schema.path("type").asText(null);
            String ref = schema.path("$ref").asText(null);

            if (ref != null) {
                // 参数类型引用了复杂对象 → 递归解析
                String refName = ref.substring(ref.lastIndexOf('/') + 1);
                JsonNode reqSchema = schemas.path(refName);

                if (type == null) type = reqSchema.path("type").asText(null);
                if (description == null) description = reqSchema.path("description").asText(null);

                mappings.add(HTTPProtocolVO.ProtocolMapping.builder()
                        .mappingType("request")
                        .parentPath(null)
                        .fieldName(name)
                        .mcpPath(name)
                        .mcpType(convertType(type))
                        .mcpDesc(description)
                        .isRequired(required ? 1 : 0)
                        .sortOrder(mappings.size() + 1)
                        .build());

                parseProperties(name, reqSchema.path("properties"), reqSchema.path("required"), schemas, mappings);
            } else {
                // 简单类型参数
                mappings.add(HTTPProtocolVO.ProtocolMapping.builder()
                        .mappingType("request")
                        .parentPath(null)
                        .fieldName(name)
                        .mcpPath(name)
                        .mcpType(convertType(type))
                        .mcpDesc(description)
                        .isRequired(required ? 1 : 0)
                        .sortOrder(mappings.size() + 1)
                        .build());
            }
        }
    }

}
