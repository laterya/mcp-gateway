package cn.laterya.ai.infrastructure.adapter.store;

import cn.laterya.ai.domain.session.adapter.store.ISessionStore;
import cn.laterya.ai.domain.session.model.SessionConfigVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 基于内存的会话存储实现。
 *
 * <p>使用 ConcurrentHashMap 作为会话容器，
 * 通过 ScheduledExecutorService 每 5 分钟扫描过期会话自动清理。
 *
 * <p>条件装配：当 {@code mcp.session.store-type=in-memory} 或未配置时激活。
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "mcp.session.store-type", havingValue = "in-memory", matchIfMissing = true)
public class InMemorySessionStore implements ISessionStore {

    private static final long SESSION_TIMEOUT_MINUTES = 30;

    private final Map<String, SessionConfigVO> sessions = new ConcurrentHashMap<>();

    private final ScheduledExecutorService cleanupScheduler =
            Executors.newSingleThreadScheduledExecutor();

    public InMemorySessionStore() {
        cleanupScheduler.scheduleAtFixedRate(
                () -> cleanupExpired(SESSION_TIMEOUT_MINUTES),
                5, 5, TimeUnit.MINUTES);
        log.info("InMemorySessionStore 已启动，超时阈值: {} 分钟", SESSION_TIMEOUT_MINUTES);
    }

    @Override
    public SessionConfigVO save(String gatewayId, SessionConfigVO sessionConfig) {
        if (sessionConfig.getSessionId() == null || sessionConfig.getSessionId().isEmpty()) {
            sessionConfig = new SessionConfigVO(UUID.randomUUID().toString(), sessionConfig.getSink());
        }
        sessions.put(sessionConfig.getSessionId(), sessionConfig);
        log.info("保存会话 gatewayId:{} sessionId:{} 当前会话数:{}",
                gatewayId, sessionConfig.getSessionId(), sessions.size());
        return sessionConfig;
    }

    @Override
    public Optional<SessionConfigVO> findById(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return Optional.empty();
        }
        SessionConfigVO sessionConfigVO = sessions.get(sessionId);
        if (sessionConfigVO != null && sessionConfigVO.isActive()) {
            return Optional.of(sessionConfigVO);
        }
        return Optional.empty();
    }

    @Override
    public void deleteById(String sessionId) {
        SessionConfigVO removed = sessions.remove(sessionId);
        if (removed != null) {
            removed.markInactive();
            log.info("删除会话 sessionId:{} 剩余会话数:{}", sessionId, sessions.size());
        }
    }

    @Override
    public int cleanupExpired(long timeoutMinutes) {
        int cleaned = 0;
        for (Map.Entry<String, SessionConfigVO> entry : sessions.entrySet()) {
            SessionConfigVO sessionConfigVO = entry.getValue();
            if (!sessionConfigVO.isActive() || sessionConfigVO.isExpired(timeoutMinutes)) {
                sessions.remove(entry.getKey());
                sessionConfigVO.markInactive();
                cleaned++;
            }
        }
        if (cleaned > 0) {
            log.info("清理了 {} 个过期会话，剩余会话数: {}", cleaned, sessions.size());
        }
        return cleaned;
    }

    @Override
    public void shutdown() {
        log.info("关闭 InMemorySessionStore...");
        sessions.clear();
        cleanupScheduler.shutdown();
        try {
            if (!cleanupScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                cleanupScheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            cleanupScheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info("InMemorySessionStore 已关闭");
    }

    @Override
    public Set<String> getAllSessionIds() {
        return Collections.unmodifiableSet(sessions.keySet());
    }
}
