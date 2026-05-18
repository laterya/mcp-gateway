package cn.laterya.ai.domain.session.service;

import cn.laterya.ai.domain.session.model.SessionConfigVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 会话管理服务实现
 *
 * <p>职责：管理 MCP 客户端的 SSE 会话生命周期（创建、查询、过期清理、关闭）。
 *
 * <p>MCP SSE 协议流程：
 * <pre>
 * 1. 客户端 GET /mcp/sse → 本服务 createSession() → 返回含 Sink 的 SessionConfigVO
 * 2. Sink 立即推送 endpoint 事件（告知客户端后续消息地址）
 * 3. Controller 把 sink.asFlux() 作为 SSE 响应返回，连接保持不断
 * 4. 客户端通过 endpoint 地址 POST 消息 → 服务端处理后通过同一 Sink 推送响应
 * 5. 会话超时或断开 → removeSession() 清理资源
 * </pre>
 *
 * <p>线程安全设计：
 * <ul>
 *   <li>ConcurrentHashMap：保证会话表读写的线程安全</li>
 *   <li>SessionConfigVO 内部字段 volatile：保证对象状态的跨线程可见性</li>
 *   <li>两者缺一不可——容器安全 ≠ 元素安全</li>
 * </ul>
 */
@Slf4j
@Service
public class SessionManagementService implements ISessionManagementService {

    /** 会话超时阈值（分钟），可抽取到 yml 配置 */
    private static final long SESSION_TIMEOUT_MINUTES = 30;

    /** 单线程定时调度器，每 5 分钟扫描过期会话 */
    private final ScheduledExecutorService cleanupScheduler = Executors.newSingleThreadScheduledExecutor();

    /** 活跃会话表，key=sessionId，value=会话配置（含 Sink） */
    private final Map<String, SessionConfigVO> activeSessions = new ConcurrentHashMap<>();

    public SessionManagementService() {
        cleanupScheduler.scheduleAtFixedRate(this::cleanupExpiredSessions, 5, 5, TimeUnit.MINUTES);
        log.info("会话管理服务已启动，会话超时时间: {} 分钟", SESSION_TIMEOUT_MINUTES);
    }

    /**
     * 创建会话
     *
     * <p>流程：生成 sessionId → 创建多播 Sink → 推送 endpoint 事件 → 存入活跃表
     *
     * <p>endpoint 事件示例：event: endpoint \n data: /gateway01/mcp/message?sessionId=abc-123
     * 客户端收到后，后续 POST 请求发往此地址。
     */
    @Override
    public SessionConfigVO createSession(String gatewayId) {
        log.info("创建会话 gatewayId:{}", gatewayId);

        String sessionId = UUID.randomUUID().toString();

        // multicast + onBackpressureBuffer：支持多订阅者，缓冲慢消费客户端的数据
        Sinks.Many<ServerSentEvent<String>> sink = Sinks.many().multicast().onBackpressureBuffer();

        // 推送 endpoint 事件——这是 SSE 协议的第一条消息，告知客户端消息请求地址
        String messageEndpoint = "/" + gatewayId + "/mcp/message?sessionId=" + sessionId;
        sink.tryEmitNext(ServerSentEvent.<String>builder()
                .event("endpoint")
                .data(messageEndpoint)
                .build());

        SessionConfigVO sessionConfigVO = new SessionConfigVO(sessionId, sink);

        activeSessions.put(sessionId, sessionConfigVO);

        log.info("创建会话 gatewayId:{} sessionId:{},当前活跃会话数:{}", gatewayId, sessionId, activeSessions.size());

        return sessionConfigVO;
    }

    /**
     * 移除会话
     *
     * <p>流程：从活跃表删除 → 标记 inactive → 关闭 Sink 流
     * 关闭 Sink 会触发 SSE 连接断开，客户端收到连接关闭事件。
     */
    @Override
    public void removeSession(String sessionId) {
        log.info("删除会话配置 sessionId:{}", sessionId);
        SessionConfigVO sessionConfigVO = activeSessions.remove(sessionId);
        if (null == sessionConfigVO) return;

        sessionConfigVO.markInactive();

        try {
            sessionConfigVO.getSink().tryEmitComplete();
        } catch (Exception e) {
            log.warn("关闭会话Sink时出错:{}", e.getMessage());
        }

        log.info("移除会话:{},剩余活跃会话数:{}", sessionId, activeSessions.size());
    }

    /**
     * 获取活跃会话
     *
     * <p>副作用：每次获取时自动刷新 lastAccessedTime，
     * 这是"过期时间基于最后访问"而非"创建时间"的关键。
     */
    @Override
    public SessionConfigVO getSession(String sessionId) {
        if (null == sessionId || sessionId.isEmpty()) {
            return null;
        }

        SessionConfigVO sessionConfigVO = activeSessions.get(sessionId);
        if (null != sessionConfigVO && sessionConfigVO.isActive()) {
            sessionConfigVO.updateLastAccessed();
            return sessionConfigVO;
        }

        return null;
    }

    /**
     * 清理过期会话
     *
     * <p>由 ScheduledExecutorService 每 5 分钟触发一次。
     * 统一调用 removeSession() 保证关闭逻辑一致（标记 inactive + 关闭 Sink）。
     */
    @Override
    public void cleanupExpiredSessions() {
        int cleanedCount = 0;

        for (Map.Entry<String, SessionConfigVO> entry : activeSessions.entrySet()) {
            SessionConfigVO sessionConfigVO = entry.getValue();

            if (!sessionConfigVO.isActive() || sessionConfigVO.isExpired(SESSION_TIMEOUT_MINUTES)) {
                removeSession(sessionConfigVO.getSessionId());
                cleanedCount++;
            }

        }

        if (cleanedCount > 0) {
            log.info("清理了 {} 个过期会话，剩余活跃会话数: {}", cleanedCount, activeSessions.size());
        }
    }

    /**
     * 优雅关闭
     *
     * <p>应用停机时调用（可接入 Spring DisposableBean 或 @PreDestroy）。
     * 流程：移除所有会话 → 停止调度器 → 等待 5 秒让正在执行的任务完成 → 超时强制关闭。
     */
    @Override
    public void shutdown() {
        log.info("关闭会话管理服务...");

        for (String sessionId : activeSessions.keySet()) {
            removeSession(sessionId);
        }

        cleanupScheduler.shutdown();

        try {
            if (!cleanupScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                cleanupScheduler.shutdown();
            }
        } catch (InterruptedException e) {
            cleanupScheduler.shutdown();
            Thread.currentThread().interrupt();
        }

        log.info("关闭会话管理服务完成");
    }

}
