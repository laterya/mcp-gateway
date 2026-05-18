package cn.laterya.ai.domain.session.service;

import cn.laterya.ai.domain.session.model.SessionConfigVO;

/**
 * 会话管理服务接口（DDD 中的 Port / 适配器接口）
 *
 * <p>定义会话的完整生命周期操作，由 SessionManagementService 实现，
 * 上层（case/trigger）通过此接口依赖倒置，不直接耦合实现类。
 *
 * <p>生命周期：createSession → getSession（多次）→ removeSession / cleanupExpiredSessions → shutdown
 */
public interface ISessionManagementService {

    /**
     * 创建会话：生成 sessionId + 初始化 Sink + 推送 endpoint 事件告知客户端消息地址
     *
     * @param gatewayId 网关标识，用于拼接消息端点 URL：/{gatewayId}/mcp/message?sessionId={sessionId}
     * @return 包含 Sink 的会话配置对象，调用方可通过 sink.asFlux() 转为 SSE 响应流
     */
    SessionConfigVO createSession(String gatewayId);

    /** 移除会话：从活跃表删除 + 标记 inactive + 关闭 Sink 流 */
    void removeSession(String sessionId);

    /** 获取活跃会话并刷新访问时间，非活跃/不存在返回 null */
    SessionConfigVO getSession(String sessionId);

    /** 扫描所有会话，清理已过期或非活跃的会话（由定时调度器触发） */
    void cleanupExpiredSessions();

    /** 优雅关闭：移除所有会话 + 停止清理调度器（应用停机时调用） */
    void shutdown();

}
