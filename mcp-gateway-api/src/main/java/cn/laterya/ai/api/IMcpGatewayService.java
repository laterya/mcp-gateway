package cn.laterya.ai.api;

import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * MCP 网关服务接口（DDD 中的 Driving Port）
 *
 * <p>定义对外暴露的 SSE 连接和消息处理能力，由 trigger 层的 Controller 实现。
 * 抽取到独立 api 模块的原因：
 * 1. 统一标准——想知道系统提供了哪些对外接口，看 api 模块即可
 * 2. 可独立打 jar 包供 RPC / 第三方集成使用
 */
public interface IMcpGatewayService {

    /**
     * 建立 SSE 连接，创建 MCP 会话
     *
     * @param gatewayId 网关唯一标识
     * @param apiKey    API 密钥（可为空）
     * @return SSE 流式响应
     */
    Flux<ServerSentEvent<String>> establishSSEConnection(String gatewayId, String apiKey);

    /**
     * 处理 MCP 消息请求
     *
     * @param gatewayId   网关唯一标识
     * @param sessionId   会话 ID
     * @param apiKey      API 密钥（可为空）
     * @param messageBody JSON-RPC 请求体
     * @return 响应结果
     */
    Mono<ResponseEntity<Void>> handleMessage(String gatewayId, String sessionId, String apiKey, String messageBody);

}
