package cn.laterya.ai.dao;

import cn.laterya.ai.infrastructure.dao.IMcpGatewayToolDao;
import cn.laterya.ai.infrastructure.dao.po.McpGatewayToolPO;
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
public class McpGatewayToolDaoTest {

    @Resource
    private IMcpGatewayToolDao mcpGatewayToolDao;

    @Test
    public void test_insert_and_queryById() {
        McpGatewayToolPO po = McpGatewayToolPO.builder()
                .gatewayId("test_gw_tool")
                .toolId(100L)
                .toolName("test_tool")
                .toolType("function")
                .toolDescription("测试工具")
                .toolVersion("1.0.0")
                .protocolId(1L)
                .protocolType("http")
                .build();
        mcpGatewayToolDao.insert(po);
        assertNotNull(po.getId());

        McpGatewayToolPO queried = mcpGatewayToolDao.queryById(po.getId());
        assertNotNull(queried);
        assertEquals("test_tool", queried.getToolName());
        assertEquals(100L, queried.getToolId());
    }

    @Test
    public void test_queryByGatewayId() {
        List<McpGatewayToolPO> list = mcpGatewayToolDao.queryByGatewayId("gateway_001");
        assertFalse(list.isEmpty());
        assertEquals("JavaSDKMCPClient_getCompanyEmployee", list.get(0).getToolName());
    }

    @Test
    public void test_queryByGatewayIdAndToolName() {
        McpGatewayToolPO result = mcpGatewayToolDao.queryByGatewayIdAndToolName("gateway_001", "JavaSDKMCPClient_getCompanyEmployee");
        assertNotNull(result);
        assertEquals("获取公司雇员信息", result.getToolDescription());
        assertEquals(1L, result.getProtocolId());
    }

    @Test
    public void test_queryByGatewayIdAndToolName_notFound() {
        McpGatewayToolPO result = mcpGatewayToolDao.queryByGatewayIdAndToolName("gateway_001", "nonexistent_tool");
        assertNull(result);
    }

    @Test
    public void test_queryAll() {
        List<McpGatewayToolPO> list = mcpGatewayToolDao.queryAll();
        assertFalse(list.isEmpty());
    }

    @Test
    public void test_update() {
        McpGatewayToolPO existing = mcpGatewayToolDao.queryByGatewayIdAndToolName("gateway_001", "JavaSDKMCPClient_getCompanyEmployee");
        assertNotNull(existing);

        existing.setToolDescription("更新后的描述");
        existing.setToolVersion("2.0.0");
        mcpGatewayToolDao.updateById(existing);

        McpGatewayToolPO updated = mcpGatewayToolDao.queryById(existing.getId());
        assertEquals("更新后的描述", updated.getToolDescription());
        assertEquals("2.0.0", updated.getToolVersion());
    }

    @Test
    public void test_delete() {
        McpGatewayToolPO po = McpGatewayToolPO.builder()
                .gatewayId("test_delete_gw")
                .toolId(999L)
                .toolName("to_delete_tool")
                .toolType("function")
                .toolDescription("待删除")
                .toolVersion("1.0.0")
                .protocolId(1L)
                .protocolType("http")
                .build();
        mcpGatewayToolDao.insert(po);
        Long id = po.getId();

        mcpGatewayToolDao.deleteById(id);
        assertNull(mcpGatewayToolDao.queryById(id));
    }
}
