package cn.laterya.ai.dao;

import cn.laterya.ai.infrastructure.dao.IMcpGatewayDao;
import cn.laterya.ai.infrastructure.dao.po.McpGatewayPO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Transactional
@Rollback
@SpringBootTest
@ActiveProfiles("test")
public class McpGatewayDaoTest {

    @Resource
    private IMcpGatewayDao mcpGatewayDao;

    @Test
    public void test_insert_and_queryById() {
        McpGatewayPO po = McpGatewayPO.builder()
                .gatewayId("test_gateway_002")
                .gatewayName("测试网关")
                .gatewayDesc("DAO测试用")
                .version("1.0.0")
                .status(1)
                .auth(0)
                .build();
        mcpGatewayDao.insert(po);
        assertNotNull(po.getId());

        McpGatewayPO queried = mcpGatewayDao.queryById(po.getId());
        assertNotNull(queried);
        assertEquals("test_gateway_002", queried.getGatewayId());
        assertEquals("测试网关", queried.getGatewayName());
    }

    @Test
    public void test_queryByGatewayId() {
        McpGatewayPO result = mcpGatewayDao.queryByGatewayId("gateway_001");
        assertNotNull(result);
        assertEquals("员工信息管理网关", result.getGatewayName());
    }

    @Test
    public void test_queryByGatewayId_notFound() {
        McpGatewayPO result = mcpGatewayDao.queryByGatewayId("nonexistent");
        assertNull(result);
    }

    @Test
    public void test_queryAll() {
        List<McpGatewayPO> list = mcpGatewayDao.queryAll();
        assertFalse(list.isEmpty());
    }

    @Test
    public void test_update() {
        McpGatewayPO existing = mcpGatewayDao.queryByGatewayId("gateway_001");
        assertNotNull(existing);

        existing.setGatewayName("更新后的网关名");
        existing.setGatewayDesc("更新后的描述");
        mcpGatewayDao.updateById(existing);

        McpGatewayPO updated = mcpGatewayDao.queryById(existing.getId());
        assertEquals("更新后的网关名", updated.getGatewayName());
        assertEquals("更新后的描述", updated.getGatewayDesc());
    }

    @Test
    public void test_delete() {
        McpGatewayPO po = McpGatewayPO.builder()
                .gatewayId("test_delete_gateway")
                .gatewayName("待删除网关")
                .status(1)
                .auth(0)
                .build();
        mcpGatewayDao.insert(po);
        Long id = po.getId();

        mcpGatewayDao.deleteById(id);
        assertNull(mcpGatewayDao.queryById(id));
    }
}
