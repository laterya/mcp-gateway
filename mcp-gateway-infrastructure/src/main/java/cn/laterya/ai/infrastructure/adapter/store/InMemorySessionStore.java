package cn.laterya.ai.infrastructure.adapter.store;

import cn.laterya.ai.domain.session.adapter.store.ISessionStore;
import cn.laterya.ai.domain.session.model.SessionConfigVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 基于内存的会话存储实现
 *
 * <p>使用 ConcurrentHashMap 存储会话，ScheduledExecutorService 定时清理过期会话。
 * 当配置 mcp.session.store-type=redis 时，此实现不会被装配。
 *
 * <p>线程安全：ConcurrentHashMap 保证容器安全，SessionConfigVO 内部 volatile 字段保证元素可见性。
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "mcp.session.store-type", havingValue = "in-memory", matchIfMissing = true)
public class InMemorySessionStore implements ISessionStore {

    private static final long DEFAULT_SESSION_TIMEOUT_MINUTES = 30;

    private final long sessionTimeoutMinutes;

    /** 单线程定时调度器，每 5 分钟扫描过期会话 */
    private final ScheduledExecutorService cleanupScheduler = Executors.newSingleThreadScheduledExecutor();

    /** 会话表，key=sessionId，value=会话配置（含 Sink） */
    private final Map<String, SessionConfigVO> sessions = new ConcurrentHashMap<>();

    public InMemorySessionStore() {
        this(DEFAULT_SESSION_TIMEOUT_MINUTES);
    }

    public InMemorySessionStore(long sessionTimeoutMinutes) {
        this.sessionTimeoutMinutes = sessionTimeoutMinutes;
        cleanupScheduler.scheduleAtFixedRate(this::cleanupExpiredSessions, 5, 5, TimeUnit.MINUTES);
        log.info("InMemorySessionStore 已启动，会话超时: {} 分钟", sessionTimeoutMinutes);
    }

    @Override
    public void save(String sessionId, SessionConfigVO sessionConfig) {
        sessions.put(sessionId, sessionConfig);
        log.debug("保存会话 sessionId:{}, 当前会话数:{}", sessionId, sessions.size());
    }

    @Override
    public Optional<SessionConfigVO> findById(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(sessions.get(sessionId));
    }

    @Override
    public SessionConfigVO remove(String sessionId) {
        SessionConfigVO removed = sessions.remove(sessionId);
        if (removed != null) {
            log.debug("移除会话 sessionId:{}, 剩余会话数:{}", sessionId, sessions.size());
        }
        return removed;
    }

    @Override
    public int cleanupExpiredSessions() {
        int cleanedCount = 0;

        for (Map.Entry<String, SessionConfigVO> entry : sessions.entrySet()) {
            SessionConfigVO sessionConfig = entry.getValue();

            if (!sessionConfig.isActive() || sessionConfig.isExpired(sessionTimeoutMinutes)) {
                sessionConfig.markInactive();
                try {
                    sessionConfig.getSink().tryEmitComplete();
                } catch (Exception e) {
                    log.warn("关闭过期会话 Sink 时出错 sessionId:{}: {}", sessionConfig.getSessionId(), e.getMessage());
                }
                sessions.remove(sessionConfig.getSessionId());
                cleanedCount++;
            }
        }

        if (cleanedCount > 0) {
            log.info("清理了 {} 个过期会话，剩余会话数: {}", cleanedCount, sessions.size());
        }

        return cleanedCount;
    }

    @Override
    public int size() {
        return sessions.size();
    }

    @Override
    public void shutdown() {
        log.info("关闭 InMemorySessionStore...");

        for (SessionConfigVO sessionConfig : sessions.values()) {
            sessionConfig.markInactive();
            try {
                sessionConfig.getSink().tryEmitComplete();
            } catch (Exception e) {
                log.warn("关闭会话 Sink 时出错 sessionId:{}: {}", sessionConfig.getSessionId(), e.getMessage());
            }
        }
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

}
