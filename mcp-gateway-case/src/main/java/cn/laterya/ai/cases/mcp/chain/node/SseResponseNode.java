package cn.laterya.ai.cases.mcp.chain.node;

import cn.laterya.ai.cases.mcp.chain.AbstractSessionChainNode;
import cn.laterya.ai.cases.mcp.chain.SessionChainContext;
import cn.laterya.ai.domain.session.model.SessionConfigVO;
import cn.laterya.ai.domain.session.service.ISessionManagementService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * SSE 响应节点 —— 链路终端，构建并返回 SSE 流
 *
 * <p>职责：
 * 1. 从上下文取出会话配置，将 sink 转为 SSE 响应流
 * 2. 合并心跳（每 60 秒推送 ping），防止连接因空闲被中间代理断开
 * 3. 注册断开回调，确保客户端断连时会话被清理
 *
 * <p>为什么是终端节点：不调用 fireNext()，链路到此结束，
 * 返回的 Flux 会一路向上传递到 Controller → 客户端。
 */
@Slf4j
@Component
public class SseResponseNode extends AbstractSessionChainNode {

    @Resource
    private ISessionManagementService sessionManagementService;

    @Override
    protected Flux<ServerSentEvent<String>> doHandle(String gatewayId, SessionChainContext context) {
        SessionConfigVO sessionConfigVO = context.getSessionConfigVO();

        // 心跳：用 SSE 标准注释行（冒号开头），MCP 客户端会自动忽略
        Flux<ServerSentEvent<String>> heartbeat = Flux.interval(Duration.ofSeconds(60))
                .map(i -> ServerSentEvent.<String>builder()
                        .comment("ping")
                        .build());

        // 合并会话 Sink 流 + 心跳，注册断连清理
        return Flux.merge(sessionConfigVO.getSink().asFlux(), heartbeat)
                .doOnCancel(() -> {
                    log.info("SSE 连接取消 sessionId:{}", sessionConfigVO.getSessionId());
                    sessionManagementService.removeSession(sessionConfigVO.getSessionId());
                })
                .doOnTerminate(() -> {
                    log.info("SSE 连接终止 sessionId:{}", sessionConfigVO.getSessionId());
                    sessionManagementService.removeSession(sessionConfigVO.getSessionId());
                });
    }

}
