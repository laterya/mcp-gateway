package cn.laterya.ai.trigger.http;

import cn.laterya.ai.cases.mcp.streamable.message.IMcpStreamableMessageService;
import cn.laterya.ai.cases.mcp.streamable.session.IMcpStreamableSessionService;
import cn.laterya.ai.cases.mcp.streamable.session.StreamableSessionChainContext;
import cn.laterya.ai.domain.session.model.McpSchemaVO;
import cn.laterya.ai.domain.session.model.SessionConfigVO;
import cn.laterya.ai.domain.session.model.entity.HandleMessageCommandEntity;
import cn.laterya.ai.domain.session.service.ISessionManagementService;
import cn.laterya.ai.types.enums.ResponseCode;
import cn.laterya.ai.types.exception.AppException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * MCP Streamable HTTP 控制器 — trigger 层
 *
 * <p>Streamable HTTP 单端点三方法设计（MCP 2025 协议，替代旧 HTTP+SSE 传输）：
 * <ul>
 *   <li>POST /{gatewayId}/mcp — initialize（无 session ID）或消息处理（有 session ID）</li>
 *   <li>GET /{gatewayId}/mcp — 打开 SSE 监听流（接收服务端推送）</li>
 *   <li>DELETE /{gatewayId}/mcp — 关闭会话</li>
 * </ul>
 *
 * <p>与 SSE 控制器的核心区别：
 * SSE 用双端点 + URL query 传 sessionId；Streamable HTTP 用单端点 + Mcp-Session-Id header。
 */
@Slf4j
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.OPTIONS})
@RequestMapping("/")
public class McpStreamableController {

    @Resource(name = "streamableMcpSessionService")
    private IMcpStreamableSessionService sessionService;

    @Resource(name = "streamableMcpMessageService")
    private IMcpStreamableMessageService messageService;

    @Resource
    private ISessionManagementService sessionManagementService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * POST — 统一入口
     *
     * <p>路由逻辑：
     * - 无 Mcp-Session-Id + body 是 initialize → 创建会话，返回 JSON-RPC 响应 + Mcp-Session-Id header
     * - 有 Mcp-Session-Id → 处理消息，响应通过 Sink SSE 推送，返回 202 Accepted
     * - 其他 → 400
     */
    @PostMapping(value = "{gatewayId}/mcp", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> handlePost(
            @PathVariable("gatewayId") String gatewayId,
            @RequestParam(value = "api_key", required = false) String apiKey,
            @RequestHeader(value = "Mcp-Session-Id", required = false) String sessionId,
            @RequestBody String messageBody) {
        try {
            if (sessionId == null || sessionId.isEmpty()) {
                // 无会话 ID → 判断是否是 initialize 请求
                McpSchemaVO.JSONRPCMessage message = McpSchemaVO.deserializeJsonRpcMessage(messageBody);
                if (message instanceof McpSchemaVO.JSONRPCRequest request && "initialize".equals(request.method())) {
                    return handleInitialize(gatewayId, apiKey, messageBody);
                }
                return ResponseEntity.badRequest()
                        .body("{\"jsonrpc\":\"2.0\",\"error\":{\"code\":-32000,\"message\":\"Bad Request: No valid session ID provided\"},\"id\":null}");
            }

            // 有会话 ID → 处理消息
            return handleMessage(gatewayId, apiKey, sessionId, messageBody);
        } catch (Exception e) {
            log.error("Streamable HTTP POST 处理失败 gatewayId:{}", gatewayId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET — SSE 监听流
     *
     * <p>客户端用 GET 打开 SSE 流接收服务端推送（通知、采样请求等）。
     * 必须携带有效的 Mcp-Session-Id header。
     */
    @GetMapping(value = "{gatewayId}/mcp", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> openSseStream(
            @PathVariable("gatewayId") String gatewayId,
            @RequestHeader(value = "Mcp-Session-Id", required = false) String sessionId) {
        try {
            log.info("Streamable HTTP 打开 SSE 监听流 gatewayId:{} sessionId:{}", gatewayId, sessionId);

            if (sessionId == null || sessionId.isEmpty()) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), "Mcp-Session-Id header is required");
            }

            SessionConfigVO session = sessionManagementService.getSession(sessionId);
            if (session == null) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), "Session not found");
            }

            Flux<ServerSentEvent<String>> heartbeat = Flux.interval(Duration.ofSeconds(60))
                    .map(i -> ServerSentEvent.<String>builder().comment("ping").build());

            return Flux.merge(session.getSink().asFlux(), heartbeat)
                    .doOnCancel(() -> {
                        log.info("Streamable HTTP SSE 监听流取消 sessionId:{}", sessionId);
                    })
                    .doOnTerminate(() -> {
                        log.info("Streamable HTTP SSE 监听流终止 sessionId:{}", sessionId);
                    });
        } catch (Exception e) {
            log.error("Streamable HTTP GET 处理失败 gatewayId:{}", gatewayId, e);
            throw e;
        }
    }

    /**
     * DELETE — 关闭会话
     */
    @DeleteMapping("{gatewayId}/mcp")
    public ResponseEntity<Void> closeSession(
            @PathVariable("gatewayId") String gatewayId,
            @RequestHeader(value = "Mcp-Session-Id", required = false) String sessionId) {
        try {
            log.info("Streamable HTTP 关闭会话 gatewayId:{} sessionId:{}", gatewayId, sessionId);
            if (sessionId != null) {
                sessionManagementService.removeSession(sessionId);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Streamable HTTP DELETE 处理失败 gatewayId:{}", gatewayId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private ResponseEntity<String> handleInitialize(String gatewayId, String apiKey, String messageBody) throws Exception {
        log.info("Streamable HTTP 处理 initialize gatewayId:{}", gatewayId);
        StreamableSessionChainContext context = sessionService.handleInitialize(gatewayId, apiKey, messageBody);

        String responseBody = objectMapper.writeValueAsString(context.getInitializeResponse());
        return ResponseEntity.ok()
                .header("Mcp-Session-Id", context.getSessionConfigVO().getSessionId())
                .body(responseBody);
    }

    private ResponseEntity<String> handleMessage(String gatewayId, String apiKey, String sessionId, String messageBody) throws Exception {
        log.info("Streamable HTTP 处理消息 gatewayId:{} sessionId:{}", gatewayId, sessionId);

        HandleMessageCommandEntity command = new HandleMessageCommandEntity(gatewayId, apiKey, sessionId, messageBody);
        messageService.handleMessage(command);
        return ResponseEntity.accepted().build();
    }

}
