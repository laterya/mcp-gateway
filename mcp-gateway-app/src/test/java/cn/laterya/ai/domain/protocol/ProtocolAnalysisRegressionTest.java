package cn.laterya.ai.domain.protocol;

import cn.laterya.ai.domain.protocol.model.entity.AnalysisCommandEntity;
import cn.laterya.ai.domain.protocol.model.valobj.enums.AnalysisTypeEnum;
import cn.laterya.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;
import cn.laterya.ai.domain.protocol.service.IProtocolAnalysis;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

/**
 * 协议解析回归测试 —— 从真实 demo-server /v3/api-docs 端点拉取 OpenAPI JSON 并解析
 *
 * <p>前提：demo-server 需在 8701 端口运行
 * <p>验证场景：
 * <ol>
 *   <li>单纯 requestBody（POST get_company_employee）</li>
 *   <li>单纯 query 参数（GET query_by_id）</li>
 *   <li>requestBody + query 参数并存（POST get_company_employee_v2）</li>
 *   <li>path 参数（GET query_by_path/{id}）</li>
 * </ol>
 */
@Slf4j
@ActiveProfiles("test")
@SpringBootTest
@EnabledIf("cn.laterya.ai.domain.protocol.DemoServerTestSupport#isDemoServerRunning")
public class ProtocolAnalysisRegressionTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Resource
    private IProtocolAnalysis protocolAnalysis;

    // ==================== 场景1：纯 requestBody ====================

    @Test
    public void test_requestBody_only() throws Exception {
        String json = DemoServerTestSupport.fetchOpenApiJson();

        AnalysisCommandEntity command = AnalysisCommandEntity.builder()
                .type(AnalysisTypeEnum.swagger)
                .openApiJson(json)
                .endpoints(List.of("/api/v1/mcp/get_company_employee"))
                .build();

        List<HTTPProtocolVO> result = protocolAnalysis.doAnalysis(command);
        log.info("[requestBody only] 解析结果:{}", objectMapper.writeValueAsString(result));

        assert !result.isEmpty() : "应至少有一个接口被解析";
        HTTPProtocolVO vo = result.get(0);
        assert "post".equals(vo.getHttpMethod()) : "方法应为 post";
        assert vo.getMappings() != null && !vo.getMappings().isEmpty() : "应有映射记录";
        log.info("✓ requestBody 解析通过，共 {} 条映射", vo.getMappings().size());
    }

    // ==================== 场景2：纯 query 参数 ====================

    @Test
    public void test_parameters_only() throws Exception {
        String json = DemoServerTestSupport.fetchOpenApiJson();

        AnalysisCommandEntity command = AnalysisCommandEntity.builder()
                .type(AnalysisTypeEnum.swagger)
                .openApiJson(json)
                .endpoints(List.of("/api/v1/mcp/query_by_id"))
                .build();

        List<HTTPProtocolVO> result = protocolAnalysis.doAnalysis(command);
        log.info("[parameters only] 解析结果:{}", objectMapper.writeValueAsString(result));

        assert !result.isEmpty();
        HTTPProtocolVO vo = result.get(0);
        assert "get".equals(vo.getHttpMethod()) : "方法应为 get";
        assert vo.getMappings() != null && !vo.getMappings().isEmpty() : "应有映射记录";
        // query 参数应有一条映射
        boolean hasId = vo.getMappings().stream().anyMatch(m -> "id".equals(m.getFieldName()));
        assert hasId : "应包含 id 参数的映射";
        log.info("✓ parameters 解析通过，共 {} 条映射", vo.getMappings().size());
    }

    // ==================== 场景3：requestBody + query 参数并存 ====================

    @Test
    public void test_requestBody_and_parameters_coexist() throws Exception {
        String json = DemoServerTestSupport.fetchOpenApiJson();

        AnalysisCommandEntity command = AnalysisCommandEntity.builder()
                .type(AnalysisTypeEnum.swagger)
                .openApiJson(json)
                .endpoints(List.of("/api/v1/mcp/get_company_employee_v2"))
                .build();

        List<HTTPProtocolVO> result = protocolAnalysis.doAnalysis(command);
        log.info("[requestBody + parameters] 解析结果:{}", objectMapper.writeValueAsString(result));

        assert !result.isEmpty();
        HTTPProtocolVO vo = result.get(0);

        // 应有 requestBody 映射（对象映射）和 parameters 映射（query 参数）
        boolean hasRequestBodyMapping = vo.getMappings().stream()
                .anyMatch(m -> "companyEmployeeRequest".equals(m.getFieldName()));
        boolean hasQueryPage = vo.getMappings().stream()
                .anyMatch(m -> "page".equals(m.getFieldName()));
        boolean hasQuerySize = vo.getMappings().stream()
                .anyMatch(m -> "size".equals(m.getFieldName()));

        assert hasRequestBodyMapping : "应包含 requestBody 映射（对象根节点）";
        assert hasQueryPage : "应包含 page query 参数";
        assert hasQuerySize : "应包含 size query 参数";
        log.info("✓ requestBody + parameters 并存解析通过，共 {} 条映射", vo.getMappings().size());
    }

    // ==================== 场景4：path 参数 ====================

    @Test
    public void test_path_parameter() throws Exception {
        String json = DemoServerTestSupport.fetchOpenApiJson();

        AnalysisCommandEntity command = AnalysisCommandEntity.builder()
                .type(AnalysisTypeEnum.swagger)
                .openApiJson(json)
                .endpoints(List.of("/api/v1/mcp/query_by_path/{id}"))
                .build();

        List<HTTPProtocolVO> result = protocolAnalysis.doAnalysis(command);
        log.info("[path parameter] 解析结果:{}", objectMapper.writeValueAsString(result));

        assert !result.isEmpty();
        HTTPProtocolVO vo = result.get(0);
        boolean hasId = vo.getMappings().stream()
                .anyMatch(m -> "id".equals(m.getFieldName()) && m.getIsRequired() == 1);
        assert hasId : "应包含 id path 参数且标记为必填";
        log.info("✓ path 参数解析通过");
    }

}
