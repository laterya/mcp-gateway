package cn.laterya.ai.domain.protocol.service.analysis.strategy;

import cn.laterya.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 协议解析策略抽象类 —— 提供两种策略的共用方法
 *
 * <p>parseProperties：递归解析嵌套对象的属性，逐层构建 mcpPath 的点分隔路径链。
 * 为什么放抽象类而不是接口 default 方法？因为 default 方法不能是 protected，
 * 而这两个方法不应对外暴露，仅子类内部使用。
 */
public abstract class AbstractProtocolAnalysisStrategy implements IProtocolAnalysisStrategy {

    /**
     * 递归解析属性节点
     *
     * @param parentMcpPath 父级 MCP 路径（如 xxxRequest01.company）
     * @param properties    properties 节点
     * @param requiredList  required 数组节点（可为 null）
     * @param schemas       components/schemas 节点（用于 $ref 解析）
     * @param mappings      结果追加列表
     */
    protected void parseProperties(String parentMcpPath, JsonNode properties, JsonNode requiredList,
                                    JsonNode schemas, List<HTTPProtocolVO.ProtocolMapping> mappings) {
        if (properties == null || properties.isMissingNode()) return;

        int sortOrder = 1;
        for (Iterator<Map.Entry<String, JsonNode>> it = properties.fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> entry = it.next();
            String propName = entry.getKey();
            JsonNode prop = entry.getValue();

            String currentMcpPath = parentMcpPath + "." + propName;

            JsonNode effectiveSchema = prop;
            String type = prop.path("type").asText(null);
            String description = prop.path("description").asText(null);

            if (prop.has("$ref")) {
                String ref = prop.path("$ref").asText();
                String refName = ref.substring(ref.lastIndexOf('/') + 1);
                effectiveSchema = schemas.path(refName);
                if (type == null) type = effectiveSchema.path("type").asText(null);
                if (description == null) description = effectiveSchema.path("description").asText(null);
            }

            mappings.add(HTTPProtocolVO.ProtocolMapping.builder()
                    .mappingType("request")
                    .parentPath(parentMcpPath)
                    .fieldName(propName)
                    .mcpPath(currentMcpPath)
                    .mcpType(convertType(type))
                    .mcpDesc(description)
                    .isRequired(containsInArray(requiredList, propName) ? 1 : 0)
                    .sortOrder(sortOrder++)
                    .build());

            if (effectiveSchema.has("properties")) {
                parseProperties(currentMcpPath, effectiveSchema.path("properties"),
                        effectiveSchema.path("required"), schemas, mappings);
            }
        }
    }

    protected String convertType(String type) {
        if (type == null) return "string";
        return switch (type.toLowerCase()) {
            case "string", "char", "date", "datetime" -> "string";
            case "integer", "int", "long", "double", "float", "number" -> "number";
            case "boolean", "bool" -> "boolean";
            case "array", "list" -> "array";
            default -> "object";
        };
    }

    protected String toLowerCamel(String name) {
        if (name == null || name.isEmpty()) return name;
        char[] cs = name.toCharArray();
        cs[0] = Character.toLowerCase(cs[0]);
        return new String(cs);
    }

    private boolean containsInArray(JsonNode array, String value) {
        if (array == null || !array.isArray()) return false;
        for (JsonNode node : array) {
            if (value.equals(node.asText())) return true;
        }
        return false;
    }

}
