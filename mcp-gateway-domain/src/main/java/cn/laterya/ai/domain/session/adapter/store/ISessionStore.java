package cn.laterya.ai.domain.session.adapter.store;

import cn.laterya.ai.domain.session.model.SessionConfigVO;

import java.util.Optional;
import java.util.Set;

/**
 * 会话存储接口（DDD 中的 Port）。
 *
 * <p>定义会话持久化的抽象契约，与存储实现（内存/Redis/数据库）解耦。
 * SessionManagementService 通过此接口委托存储操作，不再直接持有 ConcurrentHashMap。
 *
 * <p>生命周期：save → findById（多次）→ deleteById / cleanupExpired → shutdown
 */
public interface ISessionStore {

    /**
     * 保存新会话。
     *
     * @param gatewayId     网关标识，用于日志/审计
     * @param sessionConfig 会话配置（不含 sessionId 时由实现生成）
     * @return 带 sessionId 的会话配置
     */
    SessionConfigVO save(String gatewayId, SessionConfigVO sessionConfig);

    /** 按 sessionId 查找会话 */
    Optional<SessionConfigVO> findById(String sessionId);

    /** 按 sessionId 删除会话 */
    void deleteById(String sessionId);

    /** 清理过期会话，返回清理数量 */
    int cleanupExpired(long timeoutMinutes);

    /** 关闭存储，释放资源（应用停机时调用） */
    void shutdown();

    /** 返回当前所有会话 ID 集合，供服务层遍历 */
    Set<String> getAllSessionIds();
}
