package cn.laterya.ai.domain.session.service;

import cn.laterya.ai.domain.session.adapter.store.ISessionStore;
import cn.laterya.ai.domain.session.model.SessionConfigVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

import java.util.Optional;
import java.util.UUID;

/**
 * 会话管理服务实现
 *
 * <p>职责：管理 MCP 客户端的 SSE 会话生命周期（创建、查询、过期清理、关闭）。
 * 会话存储委托给可插拔的 {@link ISessionStore}，默认使用 InMemorySessionStore。
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
 * <p>线程安全由 {@link ISessionStore} 实现保证。
 */
@Slf4j
@Service
public class SessionManagementService implements ISessionManagementService {

    private static final long SESSION_TIMEOUT_MINUTES = 30;

    @Resource
    private ISessionStore sessionStore;

    /**
     * 创建会话
     *
     * <p>流程：生成 sessionId → 创建多播 Sink → 推送 endpoint 事件 → 委托 store 持久化
     *
     * <p>endpoint 事件示例：event: endpoint \n data: /gateway01/mcp/message?sessionId=abc-123
     * 客户端收到后，后续 POST 请求发往此地址。
     */
    @Override
    public SessionConfigVO createSession(String gatewayId, String apiKey) {
        log.info("创建会话 gatewayId:{}", gatewayId);

        String sessionId = UUID.randomUUID().toString();

        // multicast + onBackpressureBuffer：支持多订阅者，缓冲慢消费客户端的数据
        Sinks.Many<ServerSentEvent<String>> sink = Sinks.many().multicast().onBackpressureBuffer();

        // 推送 endpoint 事件——这是 SSE 协议的第一条消息，告知客户端消息请求地址
        // api_key 拼入 URL，客户端 POST 回调时自动携带，用于后续限流校验
        String messageEndpoint = "/api-gateway/" + gatewayId + "/mcp/sse?sessionId=" + sessionId;
        if (apiKey != null && !apiKey.isEmpty()) {
            messageEndpoint += "&api_key=" + apiKey;
        }
        sink.tryEmitNext(ServerSentEvent.<String>builder()
                .event("endpoint")
                .data(messageEndpoint)
                .build());

        SessionConfigVO sessionConfigVO = new SessionConfigVO(sessionId, sink);

        return sessionStore.save(gatewayId, sessionConfigVO);
    }

    /**
     * 移除会话
     *
     * <p>流程：从 store 查找 → 标记 inactive → 关闭 Sink 流 → 从 store 删除
     * 关闭 Sink 会触发 SSE 连接断开，客户端收到连接关闭事件。
     */
    @Override
    public void removeSession(String sessionId) {
        log.info("删除会话配置 sessionId:{}", sessionId);

        Optional<SessionConfigVO> opt = sessionStore.findById(sessionId);
        if (opt.isEmpty()) return;

        SessionConfigVO sessionConfigVO = opt.get();
        sessionConfigVO.markInactive();

        try {
            sessionConfigVO.getSink().tryEmitComplete();
        } catch (Exception e) {
            log.warn("关闭会话Sink时出错:{}", e.getMessage());
        }

        sessionStore.deleteById(sessionId);
        log.info("移除会话:{} 完成", sessionId);
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

        Optional<SessionConfigVO> opt = sessionStore.findById(sessionId);
        if (opt.isPresent()) {
            SessionConfigVO sessionConfigVO = opt.get();
            sessionConfigVO.updateLastAccessed();
            return sessionConfigVO;
        }

        return null;
    }

    /**
     * 清理过期会话
     *
     * <p>遍历所有会话，对已过期或非活跃的会话调用 removeSession() 进行完整清理。
     * InMemorySessionStore 内部也有独立的定时清理（仅移除存储条目），
     * 本方法负责连带清理 Sink 资源。
     */
    @Override
    public void cleanupExpiredSessions() {
        int cleanedCount = 0;

        for (String sessionId : sessionStore.getAllSessionIds()) {
            Optional<SessionConfigVO> opt = sessionStore.findById(sessionId);
            if (opt.isEmpty()) continue;

            SessionConfigVO sessionConfigVO = opt.get();
            if (!sessionConfigVO.isActive() || sessionConfigVO.isExpired(SESSION_TIMEOUT_MINUTES)) {
                removeSession(sessionId);
                cleanedCount++;
            }
        }

        if (cleanedCount > 0) {
            log.info("清理了 {} 个过期会话", cleanedCount);
        }
    }

    /**
     * 优雅关闭
     *
     * <p>应用停机时调用。先清理所有会话的 Sink 资源，再关闭 store。
     */
    @Override
    public void shutdown() {
        log.info("关闭会话管理服务...");

        for (String sessionId : sessionStore.getAllSessionIds()) {
            removeSession(sessionId);
        }

        sessionStore.shutdown();
        log.info("关闭会话管理服务完成");
    }

}
