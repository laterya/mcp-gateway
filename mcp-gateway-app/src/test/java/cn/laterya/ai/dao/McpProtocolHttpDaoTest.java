package cn.laterya.ai.dao;

import cn.laterya.ai.infrastructure.dao.IMcpProtocolHttpDao;
import cn.laterya.ai.infrastructure.dao.po.McpProtocolHttpPO;
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
public class McpProtocolHttpDaoTest {

    @Resource
    private IMcpProtocolHttpDao mcpProtocolHttpDao;

    @Test
    public void test_insert_and_queryById() {
        McpProtocolHttpPO po = McpProtocolHttpPO.builder()
                .protocolId(100L)
                .httpUrl("http://localhost:9999/api/test")
                .httpMethod("POST")
                .httpHeaders("{\"Content-Type\": \"application/json\"}")
                .timeout(5000)
                .retryTimes(3)
                .status(1)
                .build();
        mcpProtocolHttpDao.insert(po);
        assertNotNull(po.getId());

        McpProtocolHttpPO queried = mcpProtocolHttpDao.queryById(po.getId());
        assertNotNull(queried);
        assertEquals(100L, queried.getProtocolId());
        assertEquals("http://localhost:9999/api/test", queried.getHttpUrl());
        assertEquals("POST", queried.getHttpMethod());
        assertEquals(5000, queried.getTimeout());
        assertEquals(3, queried.getRetryTimes());
    }

    @Test
    public void test_queryByProtocolId() {
        McpProtocolHttpPO result = mcpProtocolHttpDao.queryByProtocolId(1L);
        assertNotNull(result);
        assertEquals("http://localhost:8701/api/v1/employee/query", result.getHttpUrl());
    }

    @Test
    public void test_queryByProtocolId_notFound() {
        McpProtocolHttpPO result = mcpProtocolHttpDao.queryByProtocolId(999L);
        assertNull(result);
    }

    @Test
    public void test_queryAll() {
        List<McpProtocolHttpPO> list = mcpProtocolHttpDao.queryAll();
        assertFalse(list.isEmpty());
    }

    @Test
    public void test_update() {
        McpProtocolHttpPO existing = mcpProtocolHttpDao.queryByProtocolId(1L);
        assertNotNull(existing);

        existing.setHttpUrl("http://localhost:9999/updated");
        existing.setTimeout(60000);
        mcpProtocolHttpDao.updateById(existing);

        McpProtocolHttpPO updated = mcpProtocolHttpDao.queryById(existing.getId());
        assertEquals("http://localhost:9999/updated", updated.getHttpUrl());
        assertEquals(60000, updated.getTimeout());
    }

    @Test
    public void test_delete() {
        McpProtocolHttpPO po = McpProtocolHttpPO.builder()
                .protocolId(999L)
                .httpUrl("http://localhost:9999/to-delete")
                .httpMethod("GET")
                .status(1)
                .build();
        mcpProtocolHttpDao.insert(po);
        Long id = po.getId();

        mcpProtocolHttpDao.deleteById(id);
        assertNull(mcpProtocolHttpDao.queryById(id));
    }
}
