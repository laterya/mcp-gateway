package cn.laterya.ai.dao;

import cn.laterya.ai.infrastructure.dao.IMcpGatewayAuthDao;
import cn.laterya.ai.infrastructure.dao.po.McpGatewayAuthPO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Transactional
@Rollback
@SpringBootTest
@ActiveProfiles("test")
public class McpGatewayAuthDaoTest {

    @Resource
    private IMcpGatewayAuthDao mcpGatewayAuthDao;

    @Test
    public void test_insert_and_queryById() {
        McpGatewayAuthPO po = McpGatewayAuthPO.builder()
                .gatewayId("test_gw_auth")
                .apiKey("TEST_API_KEY_123")
                .rateLimit(500)
                .expireTime(LocalDateTime.of(2029, 12, 31, 0, 0))
                .status(1)
                .build();
        mcpGatewayAuthDao.insert(po);
        assertNotNull(po.getId());

        McpGatewayAuthPO queried = mcpGatewayAuthDao.queryById(po.getId());
        assertNotNull(queried);
        assertEquals("test_gw_auth", queried.getGatewayId());
        assertEquals("TEST_API_KEY_123", queried.getApiKey());
        assertEquals(500, queried.getRateLimit());
    }

    @Test
    public void test_queryByGatewayId() {
        McpGatewayAuthPO result = mcpGatewayAuthDao.queryByGatewayId("gateway_001");
        assertNotNull(result);
        assertEquals("RS590LKPOD8877DDLMFKS4", result.getApiKey());
        assertEquals(1000, result.getRateLimit());
    }

    @Test
    public void test_queryByGatewayId_notFound() {
        McpGatewayAuthPO result = mcpGatewayAuthDao.queryByGatewayId("nonexistent");
        assertNull(result);
    }

    @Test
    public void test_queryAll() {
        List<McpGatewayAuthPO> list = mcpGatewayAuthDao.queryAll();
        assertFalse(list.isEmpty());
    }

    @Test
    public void test_update() {
        McpGatewayAuthPO existing = mcpGatewayAuthDao.queryByGatewayId("gateway_001");
        assertNotNull(existing);

        existing.setRateLimit(2000);
        existing.setStatus(0);
        mcpGatewayAuthDao.updateById(existing);

        McpGatewayAuthPO updated = mcpGatewayAuthDao.queryById(existing.getId());
        assertEquals(2000, updated.getRateLimit());
        assertEquals(0, updated.getStatus());
    }

    @Test
    public void test_delete() {
        McpGatewayAuthPO po = McpGatewayAuthPO.builder()
                .gatewayId("test_delete_auth_gw")
                .apiKey("DELETE_KEY")
                .status(1)
                .build();
        mcpGatewayAuthDao.insert(po);
        Long id = po.getId();

        mcpGatewayAuthDao.deleteById(id);
        assertNull(mcpGatewayAuthDao.queryById(id));
    }
}
