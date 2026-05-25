package cn.laterya.ai.domain.session.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Sinks;

import cn.laterya.ai.domain.session.model.enums.TransportTypeEnum;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * 会话配置值对象 —— MCP 客户端建立 SSE 连接后的运行时状态容器
 *
 * <p>为什么是 VO 不是 Entity：
 * 不落库、无事务、无数据库主键，只存在于内存 ConcurrentHashMap 中。
 * 类比：Entity 是"人"，VO 是人的"手套"——附属装饰，有生命周期但不独立持久化。
 *
 * <p>充血模型：将与自身状态相关的行为内聚到对象中，避免逻辑散落在 Service 各处。
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionConfigVO {

    /** 会话唯一标识，由 UUID 生成，作为 ConcurrentHashMap 的 key */
    private String sessionId;

    /**
     * SSE 流式推送入口 —— SSE 传输时有值，Streamable HTTP 传输时为 null
     *
     * <p>Sinks 是 Project Reactor 提供的"手动往响应式流里推数据"的入口：
     * - tryEmitNext(data)  → 推一条消息给客户端
     * - tryEmitComplete()  → 关闭 SSE 流
     *
     * <p>为什么用 multicast 而非 unicast（即使业务上 1:1）：
     * unicast 只允许一个订阅者，客户端重连或框架内部订阅时会抛异常。
     * multicast 更健壮，且 onBackpressureBuffer 能缓冲数据防丢失。
     */
    private Sinks.Many<ServerSentEvent<String>> sink;

    /** 传输方式，区分 SSE 和 Streamable HTTP */
    private TransportTypeEnum transportType;

    /** 会话创建时间（不可变） */
    private Instant createTime;

    /**
     * 最后访问时间 —— 用于过期判断
     *
     * <p>volatile 原因：ConcurrentHashMap 保证读写的线程安全，
     * 但不保证 value 对象内部字段的跨线程可见性。
     * getSession() 线程写入 → cleanupExpiredSessions() 线程读取，必须可见。
     */
    private volatile Instant lastAccessedTime;

    /**
     * 会话活跃状态
     *
     * <p>volatile 原因同上：markInactive() 写入 → getSession()/cleanup 读取。
     * 经典问题：容器线程安全 ≠ 元素线程安全。
     */
    private volatile boolean active;

    /**
     * 业务构造函数 —— 只暴露必要参数，时间戳和状态由对象自行初始化为合法值，
     * 不依赖调用者记得设置。
     *
     * <p>注意：Lombok 的 @Builder / @AllArgsConstructor 给框架反序列化用，
     * 业务代码统一走此构造函数。
     */
    public SessionConfigVO(String sessionId, Sinks.Many<ServerSentEvent<String>> sink) {
        this(sessionId, sink, TransportTypeEnum.SSE);
    }

    public SessionConfigVO(String sessionId, Sinks.Many<ServerSentEvent<String>> sink, TransportTypeEnum transportType) {
        this.sessionId = sessionId;
        this.sink = sink;
        this.transportType = transportType;
        this.createTime = Instant.now();
        this.lastAccessedTime = Instant.now();
        this.active = true;
    }

    /** 标记会话为非活跃（充血模型：行为内聚，避免外部直接 setActive） */
    public void markInactive() {
        this.active = false;
    }

    /** 刷新最后访问时间（充血模型：每次 getSession 时调用，避免散落在 Service） */
    public void updateLastAccessed() {
        this.lastAccessedTime = Instant.now();
    }

    /**
     * 过期判断（充血模型：过期策略变更只需改此处，所有调用者自动受益）
     *
     * @param timeoutMinutes 超时阈值（分钟），由 Service 层传入，可抽取到 yml 配置
     */
    public boolean isExpired(long timeoutMinutes) {
        return lastAccessedTime.isBefore(Instant.now().minus(timeoutMinutes, ChronoUnit.MINUTES));
    }

}
