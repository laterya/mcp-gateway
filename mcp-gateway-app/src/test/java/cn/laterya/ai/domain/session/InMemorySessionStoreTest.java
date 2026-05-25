package cn.laterya.ai.domain.session;

import cn.laterya.ai.domain.session.adapter.store.ISessionStore;
import cn.laterya.ai.domain.session.model.SessionConfigVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Sinks;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * InMemorySessionStore 单元测试。
 *
 * <p>直接实例化 InMemorySessionStore，不依赖 Spring 上下文。
 * 验证 CRUD、过期清理、优雅关闭等核心行为。
 */
@Slf4j
class InMemorySessionStoreTest {

    private ISessionStore sessionStore;

    @BeforeEach
    void setUp() {
        sessionStore = new cn.laterya.ai.infrastructure.adapter.store.InMemorySessionStore();
    }

    @AfterEach
    void tearDown() {
        sessionStore.shutdown();
    }

    private SessionConfigVO createTestSession(String gatewayId) {
        String sessionId = UUID.randomUUID().toString();
        Sinks.Many<org.springframework.http.codec.ServerSentEvent<String>> sink =
                Sinks.many().multicast().onBackpressureBuffer();
        return new SessionConfigVO(sessionId, sink);
    }

    // ==================== save + findById ====================

    @Test
    void test_save_session_should_be_findable() {
        SessionConfigVO sessionConfigVO = createTestSession("gateway_001");
        SessionConfigVO saved = sessionStore.save("gateway_001", sessionConfigVO);

        assertNotNull(saved);
        assertNotNull(saved.getSessionId());

        Optional<SessionConfigVO> found = sessionStore.findById(saved.getSessionId());
        assertTrue(found.isPresent());
        assertEquals(saved.getSessionId(), found.get().getSessionId());
        log.info("save + findById 正常");
    }

    @Test
    void test_findById_unknown_session_returns_empty() {
        Optional<SessionConfigVO> found = sessionStore.findById("non-existent-id");
        assertTrue(found.isEmpty());
        log.info("不存在的 sessionId 返回 empty");
    }

    // ==================== deleteById ====================

    @Test
    void test_deleteById_removes_session() {
        SessionConfigVO saved = sessionStore.save("gateway_001", createTestSession("gateway_001"));

        sessionStore.deleteById(saved.getSessionId());

        Optional<SessionConfigVO> found = sessionStore.findById(saved.getSessionId());
        assertTrue(found.isEmpty());
        log.info("删除后 findById 返回 empty");
    }

    @Test
    void test_deleteById_nonexistent_does_not_throw() {
        assertDoesNotThrow(() -> sessionStore.deleteById("non-existent-id"));
        log.info("删除不存在的 sessionId 不抛异常");
    }

    // ==================== getAllSessionIds ====================

    @Test
    void test_getAllSessionIds_returns_all_saved_ids() {
        SessionConfigVO s1 = sessionStore.save("gw1", createTestSession("gw1"));
        SessionConfigVO s2 = sessionStore.save("gw2", createTestSession("gw2"));
        SessionConfigVO s3 = sessionStore.save("gw3", createTestSession("gw3"));

        Set<String> ids = sessionStore.getAllSessionIds();
        assertEquals(3, ids.size());
        assertTrue(ids.contains(s1.getSessionId()));
        assertTrue(ids.contains(s2.getSessionId()));
        assertTrue(ids.contains(s3.getSessionId()));

        sessionStore.deleteById(s2.getSessionId());
        ids = sessionStore.getAllSessionIds();
        assertEquals(2, ids.size());
        assertFalse(ids.contains(s2.getSessionId()));
        log.info("getAllSessionIds 正确反映增删变化");
    }

    // ==================== cleanupExpired ====================

    @Test
    void test_cleanupExpired_removes_expired_sessions() throws Exception {
        SessionConfigVO saved = sessionStore.save("gw", createTestSession("gw"));

        // timeoutMinutes=0：任何 lastAccessedTime < now 的会话都视为过期
        Thread.sleep(2);

        int cleaned = sessionStore.cleanupExpired(0);
        log.info("清理了 {} 个过期会话", cleaned);

        Optional<SessionConfigVO> found = sessionStore.findById(saved.getSessionId());
        assertTrue(found.isEmpty(), "过期会话应该被清理");
    }

    @Test
    void test_cleanupExpired_returns_zero_when_none_expired() {
        sessionStore.save("gw", createTestSession("gw"));

        // 使用极大超时（9999 分钟）确保不过期
        int cleaned = sessionStore.cleanupExpired(9999);
        assertEquals(0, cleaned, "没有会话应该被清理");
        assertEquals(1, sessionStore.getAllSessionIds().size());
        log.info("未过期会话不被清理");
    }

    // ==================== shutdown ====================

    @Test
    void test_shutdown_clears_all_and_stops_scheduler() {
        sessionStore.save("gw1", createTestSession("gw1"));
        sessionStore.save("gw2", createTestSession("gw2"));

        sessionStore.shutdown();

        // shutdown 后 getAllSessionIds 应返回空
        Set<String> ids = sessionStore.getAllSessionIds();
        assertTrue(ids.isEmpty(), "shutdown 后应无会话");

        // shutdown 可重复调用不抛异常
        assertDoesNotThrow(() -> sessionStore.shutdown());
        log.info("shutdown 清空会话且可重复调用");
    }

    // ==================== save with generated sessionId ====================

    @Test
    void test_save_generates_sessionId_when_null() {
        Sinks.Many<org.springframework.http.codec.ServerSentEvent<String>> sink =
                Sinks.many().multicast().onBackpressureBuffer();
        SessionConfigVO sessionConfigVO = new SessionConfigVO(null, sink);

        SessionConfigVO saved = sessionStore.save("gw", sessionConfigVO);

        assertNotNull(saved.getSessionId());
        assertFalse(saved.getSessionId().isEmpty());
        log.info("sessionId 为 null 时自动生成: {}", saved.getSessionId());
    }
}
