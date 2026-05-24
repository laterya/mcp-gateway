package cn.laterya.ai.domain.protocol;

import cn.laterya.ai.domain.protocol.model.entity.AnalysisCommandEntity;
import cn.laterya.ai.domain.protocol.model.valobj.enums.AnalysisTypeEnum;
import cn.laterya.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;
import cn.laterya.ai.domain.protocol.service.IProtocolAnalysis;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 协议解析测试 —— 验证 IProtocolAnalysis 策略模式端到端解析
 */
@Slf4j
@ActiveProfiles("test")
@SpringBootTest
public class ProtocolAnalysisTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Resource
    private IProtocolAnalysis protocolAnalysis;

    @Test
    public void test_parse_getCompanyEmployee() throws Exception {
        String json = new String(new ClassPathResource("swagger/api-docs-test03.json").getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        AnalysisCommandEntity commandEntity = AnalysisCommandEntity.builder()
                .type(AnalysisTypeEnum.swagger)
                .openApiJson(json)
                .endpoints(List.of("/api/v1/mcp/get_company_employee"))
                .build();

        List<HTTPProtocolVO> result = protocolAnalysis.doAnalysis(commandEntity);
        log.info("测试结果:{}", objectMapper.writeValueAsString(result));
    }

}
