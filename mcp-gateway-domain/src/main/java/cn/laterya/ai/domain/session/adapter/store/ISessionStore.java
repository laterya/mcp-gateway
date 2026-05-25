package cn.laterya.ai.domain.session.adapter.store;

import cn.laterya.ai.domain.session.model.SessionConfigVO;

import java.util.Optional;

/**
 * 会话存储抽象接口 —— 定义会话持久化的可插拔契约
 *
 * <p>当前提供 InMemory（ConcurrentHashMap）默认实现，后续可扩展 Redis 等实现。
 * 通过 Spring @ConditionalOnProperty 在运行时选择具体实现。
 *
 * <p>所有实现必须保证线程安全。
 */
public interface ISessionStore {

    /**
     * 保存会话配置
     *
     * @param sessionId 会话唯一标识
     * @param sessionConfig 会话配置（含 Sink）
     */
    void save(String sessionId, SessionConfigVO sessionConfig);

    /**
     * 按 ID 查找会话
     *
     * @param sessionId 会话唯一标识
     * @return 会话配置，不存在返回 Optional.empty()
     */
    Optional<SessionConfigVO> findById(String sessionId);

    /**
     * 移除会话
     *
     * @param sessionId 会话唯一标识
     * @return 被移除的会话配置，不存在返回 null
     */
    SessionConfigVO remove(String sessionId);

    /**
     * 清理过期会话并从存储中移除
     *
     * @return 本次清理的会话数量
     */
    int cleanupExpiredSessions();

    /** 当前存储中的会话总数 */
    int size();

    /** 优雅关闭，释放存储资源（如定时调度器） */
    void shutdown();

}
