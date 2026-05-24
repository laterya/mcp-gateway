package cn.laterya.ai.domain.protocol;

import cn.laterya.ai.domain.protocol.model.entity.AnalysisCommandEntity;
import cn.laterya.ai.domain.protocol.model.entity.StorageCommandEntity;
import cn.laterya.ai.domain.protocol.model.valobj.enums.AnalysisTypeEnum;
import cn.laterya.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;
import cn.laterya.ai.domain.protocol.service.IProtocolAnalysis;
import cn.laterya.ai.domain.protocol.service.IProtocolStorage;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * 协议存储测试 —— 完整链路：拉取 demo-server OpenAPI JSON → 解析 → 落库
 *
 * <p>前提：demo-server 需在 8701 端口运行
 * <p>验证点：
 * <ol>
 *   <li>解析 get_company_employee（纯 requestBody）→ 存库 → 返回 protocolId</li>
 *   <li>解析 query_by_id（纯 query）→ 存库 → 返回 protocolId</li>
 *   <li>解析 get_company_employee_v2（requestBody + parameters 并存）→ 存库 → 验证映射条数</li>
 * </ol>
 */
@Slf4j
@ActiveProfiles("test")
@SpringBootTest
public class ProtocolStorageTest {

    @Resource
    private IProtocolAnalysis protocolAnalysis;

    @Resource
    private IProtocolStorage protocolStorage;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String fetchOpenApiJson() throws Exception {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8701/v3/api-docs"))
                    .GET().build();
            return client.send(req, HttpResponse.BodyHandlers.ofString()).body();
        }
    }

    /**
     * 纯 requestBody 接口的解析+存储
     */
    @Test
    public void test_store_requestBody_only() throws Exception {
        String json = fetchOpenApiJson();

        // 1. 解析
        List<HTTPProtocolVO> vos = protocolAnalysis.doAnalysis(
                AnalysisCommandEntity.builder()
                        .type(AnalysisTypeEnum.swagger)
                        .openApiJson(json)
                        .endpoints(List.of("/api/v1/mcp/get_company_employee"))
                        .build());
        log.info("[存储-纯requestBody] 解析结果:{}", objectMapper.writeValueAsString(vos));
        assert !vos.isEmpty();

        // 2. 存储
        List<Long> protocolIds = protocolStorage.doStorage(
                StorageCommandEntity.builder().httpProtocolVOS(vos).build());
        log.info("[存储-纯requestBody] protocolId:{}", protocolIds);
        assert protocolIds.size() == 1 : "应返回 1 个 protocolId";
        assert protocolIds.get(0) > 0 : "protocolId 应 > 0";
    }

    /**
     * 纯 query 参数接口的解析+存储
     */
    @Test
    public void test_store_parameters_only() throws Exception {
        String json = fetchOpenApiJson();

        List<HTTPProtocolVO> vos = protocolAnalysis.doAnalysis(
                AnalysisCommandEntity.builder()
                        .type(AnalysisTypeEnum.swagger)
                        .openApiJson(json)
                        .endpoints(List.of("/api/v1/mcp/query_by_id"))
                        .build());

        List<Long> protocolIds = protocolStorage.doStorage(
                StorageCommandEntity.builder().httpProtocolVOS(vos).build());
        log.info("[存储-纯parameters] protocolId:{}", protocolIds);
        assert !protocolIds.isEmpty();
    }

    /**
     * requestBody + parameters 并存接口的解析+存储（关键验证）
     */
    @Test
    public void test_store_requestBody_and_parameters() throws Exception {
        String json = fetchOpenApiJson();

        List<HTTPProtocolVO> vos = protocolAnalysis.doAnalysis(
                AnalysisCommandEntity.builder()
                        .type(AnalysisTypeEnum.swagger)
                        .openApiJson(json)
                        .endpoints(List.of("/api/v1/mcp/get_company_employee_v2"))
                        .build());

        // 验证解析出了 requestBody 映射 + query 参数映射
        HTTPProtocolVO vo = vos.get(0);
        long requestBodyMappings = vo.getMappings().stream()
                .filter(m -> m.getParentPath() != null || "companyEmployeeRequest".equals(m.getFieldName()))
                .count();
        long queryMappings = vo.getMappings().stream()
                .filter(m -> "page".equals(m.getFieldName()) || "size".equals(m.getFieldName()))
                .count();
        log.info("[存储-并存] requestBody映射:{}条 query参数:{}条 总映射:{}条",
                requestBodyMappings, queryMappings, vo.getMappings().size());
        assert requestBodyMappings > 0 && queryMappings > 0 : "应有 requestBody 和 parameters 两种映射";

        List<Long> protocolIds = protocolStorage.doStorage(
                StorageCommandEntity.builder().httpProtocolVOS(vos).build());
        log.info("[存储-并存] protocolId:{}", protocolIds);
        assert !protocolIds.isEmpty();
    }

}
