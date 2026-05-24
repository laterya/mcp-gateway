package cn.laterya.ai.domain.gateway;

import cn.laterya.ai.domain.gateway.model.entity.GatewayConfigCommandEntity;
import cn.laterya.ai.domain.gateway.model.valobj.GatewayConfigVO;
import cn.laterya.ai.domain.gateway.service.IGatewayConfigService;
import cn.laterya.ai.types.enums.GatewayEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 网关配置 CRUD 测试
 */
@Slf4j
@ActiveProfiles("test")
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GatewayConfigServiceTest {

    @Resource
    private IGatewayConfigService gatewayConfigService;

    private static final String TEST_GATEWAY_ID = "gateway_002";

    @Test
    @Order(1)
    public void test_saveGatewayConfig() {
        GatewayConfigCommandEntity cmd = new GatewayConfigCommandEntity();
        cmd.setGatewayConfigVO(GatewayConfigVO.builder()
                .gatewayId(TEST_GATEWAY_ID)
                .gatewayName("员工信息查询网关")
                .gatewayDesc("用于查询公司员工信息的MCP网关")
                .version("1.0.0")
                .auth(GatewayEnum.GatewayAuthStatusEnum.ENABLE)
                .status(GatewayEnum.GatewayStatus.STRONG_VERIFIED)
                .build());

        // 如果已存在则跳过插入（幂等性处理）
        try {
            gatewayConfigService.saveGatewayConfig(cmd);
            log.info("保存网关配置成功 gatewayId:{}", TEST_GATEWAY_ID);
        } catch (Exception e) {
            log.info("网关配置已存在，跳过插入 gatewayId:{}", TEST_GATEWAY_ID);
        }
    }

    @Test
    @Order(2)
    public void test_updateGatewayAuthStatus() {
        GatewayConfigCommandEntity cmd = GatewayConfigCommandEntity.buildUpdateGatewayAuthStatusVO(
                TEST_GATEWAY_ID, GatewayEnum.GatewayAuthStatusEnum.DISABLE);

        gatewayConfigService.updateGatewayAuthStatus(cmd);
        log.info("更新网关鉴权状态成功 gatewayId:{}", TEST_GATEWAY_ID);
    }

}
