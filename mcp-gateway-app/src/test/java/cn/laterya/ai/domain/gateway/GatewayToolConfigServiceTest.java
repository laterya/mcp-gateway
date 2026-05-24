package cn.laterya.ai.domain.gateway;

import cn.laterya.ai.domain.gateway.model.entity.GatewayToolConfigCommandEntity;
import cn.laterya.ai.domain.gateway.model.valobj.GatewayToolConfigVO;
import cn.laterya.ai.domain.gateway.service.IGatewayToolConfigService;
import cn.laterya.ai.types.enums.GatewayEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 网关工具配置 CRUD 测试
 */
@Slf4j
@ActiveProfiles("test")
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GatewayToolConfigServiceTest {

    @Resource
    private IGatewayToolConfigService gatewayToolConfigService;

    private static final String TEST_GATEWAY_ID = "gateway_002";
    // 随机 toolName 避免重复键冲突
    private static final String TOOL_NAME = "JavaSDKMCPClient_getCompanyEmployee_" + RandomStringUtils.randomAlphanumeric(4);

    @Test
    @Order(1)
    public void test_saveGatewayToolConfig() {
        GatewayToolConfigCommandEntity cmd = new GatewayToolConfigCommandEntity();
        cmd.setGatewayToolConfigVO(GatewayToolConfigVO.builder()
                .gatewayId(TEST_GATEWAY_ID)
                .toolId(Long.valueOf(RandomStringUtils.randomNumeric(4)))
                .toolName(TOOL_NAME)
                .toolType("function")
                .toolDescription("获取公司雇员信息")
                .toolVersion("1.0.0")
                .protocolId(87971207L)
                .protocolType("http")
                .build());

        gatewayToolConfigService.saveGatewayToolConfig(cmd);
        log.info("保存网关工具配置成功 gatewayId:{} toolId:{}",
                cmd.getGatewayToolConfigVO().getGatewayId(), cmd.getGatewayToolConfigVO().getToolId());
    }

    @Test
    @Order(2)
    public void test_updateGatewayToolProtocol() {
        GatewayToolConfigCommandEntity cmd = GatewayToolConfigCommandEntity.buildUpdateGatewayProtocol(
                TEST_GATEWAY_ID, 4904L, 87971207L, "http");

        gatewayToolConfigService.updateGatewayToolProtocol(cmd);
        log.info("更新网关工具协议成功 gatewayId:{} protocolId:{}",
                cmd.getGatewayToolConfigVO().getGatewayId(), cmd.getGatewayToolConfigVO().getProtocolId());
    }

}
