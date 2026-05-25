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
 * 存储操作委托给可插拔的 {@link ISessionStore}。
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
 * <p>线程安全：由 {@link ISessionStore} 的具体实现保证。
 */
@Slf4j
@Service
public class SessionManagementService implements ISessionManagementService {

    @Resource
    private ISessionStore sessionStore;

    /**
     * 创建会话
     *
     * <p>流程：生成 sessionId → 创建多播 Sink → 推送 endpoint 事件 → 委托 sessionStore 存储
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
        sessionStore.save(sessionId, sessionConfigVO);

        log.info("创建会话 gatewayId:{} sessionId:{}, 当前活跃会话数:{}", gatewayId, sessionId, sessionStore.size());

        return sessionConfigVO;
    }

    /**
     * 移除会话
     *
     * <p>流程：从 sessionStore 查找 → 标记 inactive → 关闭 Sink 流 → 从 sessionStore 删除。
     * 关闭 Sink 会触发 SSE 连接断开，客户端收到连接关闭事件。
     */
    @Override
    public void removeSession(String sessionId) {
        log.info("删除会话配置 sessionId:{}", sessionId);
        Optional<SessionConfigVO> found = sessionStore.findById(sessionId);
        if (found.isEmpty()) {
            return;
        }

        SessionConfigVO sessionConfigVO = found.get();
        sessionConfigVO.markInactive();

        try {
            sessionConfigVO.getSink().tryEmitComplete();
        } catch (Exception e) {
            log.warn("关闭会话Sink时出错:{}", e.getMessage());
        }

        sessionStore.remove(sessionId);
        log.info("移除会话:{}, 剩余活跃会话数:{}", sessionId, sessionStore.size());
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

        Optional<SessionConfigVO> found = sessionStore.findById(sessionId);
        if (found.isPresent()) {
            SessionConfigVO sessionConfigVO = found.get();
            if (sessionConfigVO.isActive()) {
                sessionConfigVO.updateLastAccessed();
                return sessionConfigVO;
            }
        }

        return null;
    }

    /**
     * 清理过期会话
     *
     * <p>委托给 sessionStore 执行扫描和清理，sessionStore 内部会调用 markInactive + tryEmitComplete。
     */
    @Override
    public void cleanupExpiredSessions() {
        sessionStore.cleanupExpiredSessions();
    }

    /**
     * 优雅关闭
     *
     * <p>委托给 sessionStore 执行资源释放（移除所有会话 + 停止调度器）。
     */
    @Override
    public void shutdown() {
        log.info("关闭会话管理服务...");
        sessionStore.shutdown();
        log.info("关闭会话管理服务完成");
    }

}
