package cn.laterya.ai.dao;

import cn.laterya.ai.infrastructure.dao.IMcpProtocolMappingDao;
import cn.laterya.ai.infrastructure.dao.po.McpProtocolMappingPO;
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
public class McpProtocolMappingDaoTest {

    @Resource
    private IMcpProtocolMappingDao mcpProtocolMappingDao;

    @Test
    public void test_insert_and_queryById() {
        McpProtocolMappingPO po = McpProtocolMappingPO.builder()
                .protocolId(100L)
                .mappingType("request")
                .parentPath(null)
                .fieldName("testField")
                .mcpPath("testField")
                .mcpType("string")
                .mcpDesc("测试字段")
                .isRequired(1)
                .sortOrder(1)
                .build();
        mcpProtocolMappingDao.insert(po);
        assertNotNull(po.getId());

        McpProtocolMappingPO queried = mcpProtocolMappingDao.queryById(po.getId());
        assertNotNull(queried);
        assertEquals("testField", queried.getFieldName());
        assertEquals("string", queried.getMcpType());
    }

    @Test
    public void test_queryByProtocolId() {
        List<McpProtocolMappingPO> list = mcpProtocolMappingDao.queryByProtocolId(1L);
        assertFalse(list.isEmpty());
        assertEquals(14, list.size());
    }

    @Test
    public void test_queryByProtocolId_notFound() {
        List<McpProtocolMappingPO> list = mcpProtocolMappingDao.queryByProtocolId(999L);
        assertTrue(list.isEmpty());
    }

    @Test
    public void test_queryAll() {
        List<McpProtocolMappingPO> list = mcpProtocolMappingDao.queryAll();
        assertFalse(list.isEmpty());
    }

    @Test
    public void test_insert_withParentPath() {
        McpProtocolMappingPO po = McpProtocolMappingPO.builder()
                .protocolId(100L)
                .mappingType("request")
                .parentPath("xxxRequest01")
                .fieldName("nestedField")
                .mcpPath("xxxRequest01.nestedField")
                .mcpType("number")
                .mcpDesc("嵌套字段")
                .isRequired(0)
                .sortOrder(3)
                .build();
        mcpProtocolMappingDao.insert(po);

        McpProtocolMappingPO queried = mcpProtocolMappingDao.queryById(po.getId());
        assertEquals("xxxRequest01", queried.getParentPath());
        assertEquals("xxxRequest01.nestedField", queried.getMcpPath());
    }

    @Test
    public void test_update() {
        List<McpProtocolMappingPO> list = mcpProtocolMappingDao.queryByProtocolId(1L);
        McpProtocolMappingPO existing = list.get(0);

        existing.setMcpDesc("更新后的描述");
        existing.setIsRequired(0);
        mcpProtocolMappingDao.updateById(existing);

        McpProtocolMappingPO updated = mcpProtocolMappingDao.queryById(existing.getId());
        assertEquals("更新后的描述", updated.getMcpDesc());
        assertEquals(0, updated.getIsRequired());
    }

    @Test
    public void test_delete() {
        McpProtocolMappingPO po = McpProtocolMappingPO.builder()
                .protocolId(999L)
                .mappingType("request")
                .fieldName("toDelete")
                .mcpPath("toDelete")
                .mcpType("string")
                .isRequired(0)
                .sortOrder(0)
                .build();
        mcpProtocolMappingDao.insert(po);
        Long id = po.getId();

        mcpProtocolMappingDao.deleteById(id);
        assertNull(mcpProtocolMappingDao.queryById(id));
    }
}
