package cn.laterya.ai.domain.protocol.service.analysis;

import cn.laterya.ai.domain.protocol.model.entity.AnalysisCommandEntity;
import cn.laterya.ai.domain.protocol.model.valobj.enums.AnalysisTypeEnum;
import cn.laterya.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;
import cn.laterya.ai.domain.protocol.service.IProtocolAnalysis;
import cn.laterya.ai.domain.protocol.service.analysis.strategy.IProtocolAnalysisStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 协议解析服务 —— 策略模式编排中心
 *
 * <p>职责：将 OpenAPI JSON 解析为 HTTPProtocolVO 列表。
 * 流程：提取 baseUrl/paths/schemas → 遍历 endpoints → 枚举判断策略 → Map 取出策略执行 → 组装 VO。
 *
 * <p>策略分发依赖 Spring 自动注入 Map<String, IProtocolAnalysisStrategy>：
 * key = Bean 名（@Component("requestBodyAnalysis")）= 枚举 code，零配置精确匹配。
 */
@Slf4j
@Service
public class ProtocolAnalysis implements IProtocolAnalysis {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * key = 策略 Bean 名（如 "requestBodyAnalysis"），value = 策略实现
     */
    private final Map<String, IProtocolAnalysisStrategy> protocolAnalysisStrategyMap;

    public ProtocolAnalysis(Map<String, IProtocolAnalysisStrategy> protocolAnalysisStrategyMap) {
        this.protocolAnalysisStrategyMap = protocolAnalysisStrategyMap;
    }

    @Override
    public List<HTTPProtocolVO> doAnalysis(AnalysisCommandEntity commandEntity) {
        log.info("协议解析请求 endpoints:{}", commandEntity.getEndpoints());

        List<HTTPProtocolVO> list = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(commandEntity.getOpenApiJson());

            String baseUrl = root.path("servers").get(0).path("url").asText();

            JsonNode paths = root.path("paths");
            JsonNode schemas = root.path("components").path("schemas");

            List<String> endpoints = commandEntity.getEndpoints();
            if (null == endpoints || endpoints.isEmpty()) return list;

            for (String endpoint : endpoints) {
                JsonNode pathItem = paths.path(endpoint);
                if (pathItem.isMissingNode()) continue;

                String method = detectMethod(pathItem);
                JsonNode operation = pathItem.path(method);

                HTTPProtocolVO vo = new HTTPProtocolVO();
                vo.setHttpUrl(baseUrl + endpoint);
                vo.setHttpMethod(method);
                vo.setHttpHeaders("{\"Content-Type\":\"application/json\"}");
                vo.setTimeout(30000);

                List<HTTPProtocolVO.ProtocolMapping> mappings = new ArrayList<>();

                // 充血枚举返回适用策略列表：一个 operation 可能同时含 requestBody + parameters
                // （如 POST /endpoint?page=1&size=10 + JSON body），两者都需解析
                List<AnalysisTypeEnum.SwaggerAnalysisAction> actions = AnalysisTypeEnum.SwaggerAnalysisAction.getApplicableStrategies(operation);
                for (AnalysisTypeEnum.SwaggerAnalysisAction action : actions) {
                    IProtocolAnalysisStrategy strategy = protocolAnalysisStrategyMap.get(action.getCode());
                    strategy.doAnalysis(operation, schemas, mappings);
                }

                vo.setMappings(mappings);
                list.add(vo);
            }

        } catch (Exception e) {
            log.error("协议解析失败 endpoints:{}", commandEntity.getEndpoints(), e);
        }

        return list;
    }

    private String detectMethod(JsonNode pathItem) {
        if (pathItem.has("post")) return "post";
        if (pathItem.has("get")) return "get";
        if (pathItem.has("put")) return "put";
        if (pathItem.has("delete")) return "delete";
        return "post";
    }

}
