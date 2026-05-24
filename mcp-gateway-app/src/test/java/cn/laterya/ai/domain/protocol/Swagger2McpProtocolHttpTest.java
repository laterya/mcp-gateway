package cn.laterya.ai.domain.protocol;

import cn.laterya.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Swagger OpenAPI JSON 解析测试
 *
 * <p>将 Swagger 导出的 OpenAPI 标准 JSON 转换为 {@link HTTPProtocolVO}，
 * 验证字段映射关系是否正确，后续章节再做落库。
 */
@Slf4j
public class Swagger2McpProtocolHttpTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void test_parse_getCompanyEmployee() throws Exception {
        String json = new String(new ClassPathResource("swagger/api-docs-test03.json").getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        List<String> endpoints = List.of("/api/v1/mcp/get_company_employee");
        List<HTTPProtocolVO> result = parse(json, endpoints);
        log.info("解析结果:{}", objectMapper.writeValueAsString(result));
    }

    List<HTTPProtocolVO> parse(String json, List<String> endpoints) throws Exception {
        JsonNode root = objectMapper.readTree(json);

        String baseUrl = root.path("servers").get(0).path("url").asText();

        JsonNode paths = root.path("paths");
        JsonNode schemas = root.path("components").path("schemas");

        List<HTTPProtocolVO> list = new ArrayList<>();

        for (String endpoint : endpoints) {
            JsonNode pathItem = paths.path(endpoint);
            if (pathItem.isMissingNode()) continue;

            String method = detectMethod(pathItem);
            JsonNode operation = pathItem.path(method);

            HTTPProtocolVO vo = new HTTPProtocolVO();
            vo.setHttpUrl(baseUrl + endpoint);
            vo.setHttpMethod(method);
            // 默认 JSON 请求头
            vo.setHttpHeaders("{\"Content-Type\":\"application/json\"}");
            vo.setTimeout(30000);

            List<HTTPProtocolVO.ProtocolMapping> mappings = new ArrayList<>();

            // 1. 解析 requestBody — 对象入参
            JsonNode requestBody = operation.path("requestBody");
            if (!requestBody.isMissingNode()) {
                JsonNode appJson = requestBody.path("content").path("application/json");
                if (!appJson.isMissingNode()) {
                    String ref = appJson.path("schema").path("$ref").asText(null);
                    if (ref != null) {
                        String refName = ref.substring(ref.lastIndexOf('/') + 1);
                        JsonNode reqSchema = schemas.path(refName);
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
            }

            // 2. 解析 parameters — 属性入参
            JsonNode parameters = operation.path("parameters");
            if (parameters.isArray()) {
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

            vo.setMappings(mappings);
            list.add(vo);
        }
        return list;
    }

    private void parseProperties(String parentMcpPath, JsonNode properties, JsonNode requiredList,
                                  JsonNode definitions, List<HTTPProtocolVO.ProtocolMapping> mappings) {
        if (properties == null || properties.isMissingNode()) return;

        int sortOrder = 1;
        for (var it = properties.fields(); it.hasNext(); ) {
            var entry = it.next();
            String propName = entry.getKey();
            JsonNode prop = entry.getValue();

            String currentMcpPath = parentMcpPath + "." + propName;

            JsonNode effectiveSchema = prop;
            String type = prop.path("type").asText(null);
            String description = prop.path("description").asText(null);

            // 处理 $ref 引用
            if (prop.has("$ref")) {
                String ref = prop.path("$ref").asText();
                String refName = ref.substring(ref.lastIndexOf('/') + 1);
                effectiveSchema = definitions.path(refName);
                if (type == null) type = effectiveSchema.path("type").asText(null);
                if (description == null) description = effectiveSchema.path("description").asText(null);
            }

            boolean isRequired = false;
            if (requiredList != null && requiredList.isArray()) {
                for (JsonNode r : requiredList) {
                    if (propName.equals(r.asText())) {
                        isRequired = true;
                        break;
                    }
                }
            }

            mappings.add(HTTPProtocolVO.ProtocolMapping.builder()
                    .mappingType("request")
                    .parentPath(parentMcpPath)
                    .fieldName(propName)
                    .mcpPath(currentMcpPath)
                    .mcpType(convertType(type))
                    .mcpDesc(description)
                    .isRequired(isRequired ? 1 : 0)
                    .sortOrder(sortOrder++)
                    .build());

            // 嵌套对象递归解析
            if (effectiveSchema.has("properties")) {
                parseProperties(currentMcpPath, effectiveSchema.path("properties"),
                        effectiveSchema.path("required"), definitions, mappings);
            }
        }
    }

    /** MCP数据类型映射：string/number/boolean/object/array */
    private String convertType(String type) {
        if (type == null) return "string";
        return switch (type.toLowerCase()) {
            case "string", "char", "date", "datetime" -> "string";
            case "integer", "int", "long", "double", "float", "number" -> "number";
            case "boolean", "bool" -> "boolean";
            case "array", "list" -> "array";
            default -> "object";
        };
    }

    /** 检测 HTTP 方法：优先 post → get → put → delete */
    private String detectMethod(JsonNode pathItem) {
        if (pathItem.has("post")) return "post";
        if (pathItem.has("get")) return "get";
        if (pathItem.has("put")) return "put";
        if (pathItem.has("delete")) return "delete";
        return "post";
    }

    private String toLowerCamel(String name) {
        if (name == null || name.isEmpty()) return name;
        char[] cs = name.toCharArray();
        cs[0] = Character.toLowerCase(cs[0]);
        return new String(cs);
    }

}
