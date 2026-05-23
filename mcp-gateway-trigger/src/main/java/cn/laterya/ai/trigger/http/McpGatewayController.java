package cn.laterya.ai.trigger.http;

import cn.laterya.ai.api.IMcpGatewayService;
import cn.laterya.ai.cases.mcp.IMcpSessionService;
import cn.laterya.ai.domain.session.model.McpSchemaVO;
import cn.laterya.ai.domain.session.model.SessionConfigVO;
import cn.laterya.ai.domain.session.service.ISessionManagementService;
import cn.laterya.ai.domain.session.service.ISessionMessageService;
import cn.laterya.ai.types.enums.ResponseCode;
import cn.laterya.ai.types.exception.AppException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * MCP 网关控制器 —— trigger 层
 *
 * <p>职责（只做接口封装，不含业务逻辑）：
 * 1. 日志打印
 * 2. 参数校验
 * 3. 调用 case/domain 层
 * 4. 通过 SSE Sink 推送响应
 *
 * <p>SSE 双通道设计：
 * <ul>
 *   <li>GET /{gatewayId}/mcp/sse → 建立 SSE 流（服务端推送通道）</li>
 *   <li>POST /{gatewayId}/mcp/sse?sessionId=xxx → 客户端发送消息（请求通道）</li>
 * </ul>
 * 客户端 POST 的响应不是通过 HTTP 返回，而是通过同一个 SSE Sink 推送回去。
 */
@Slf4j
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@RequestMapping("/")
public class McpGatewayController implements IMcpGatewayService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Resource
    private IMcpSessionService mcpSessionService;

    // todo 暂时调用 domain 测试，后续调用 case 编排
    @Resource
    private ISessionMessageService sessionMessageService;

    @Resource
    private ISessionManagementService sessionManagementService;

    @Override
    @GetMapping(value = "{gatewayId}/mcp/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> establishSSEConnection(@PathVariable("gatewayId") String gatewayId) {
        try {
            log.info("建立 MCP SSE 连接 gatewayId:{}", gatewayId);

            if (!StringUtils.hasText(gatewayId)) {
                log.info("非法参数，gatewayId 为空");
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }

            return mcpSessionService.createMcpSession(gatewayId);
        } catch (Exception e) {
            log.error("建立 MCP SSE 连接失败 gatewayId:{}", gatewayId, e);
            throw e;
        }
    }

    @Override
    @PostMapping(value = "{gatewayId}/mcp/sse", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Void>> handleMessage(@PathVariable("gatewayId") String gatewayId,
                                                     @RequestParam String sessionId,
                                                     @RequestBody String messageBody) {
        try {
            log.info("处理 MCP SSE 消息 gatewayId:{} sessionId:{} messageBody:{}", gatewayId, sessionId, messageBody);

            // 查找会话，不存在则拒绝
            SessionConfigVO session = sessionManagementService.getSession(sessionId);
            if (null == session) {
                log.warn("会话不存在或已过期 gatewayId:{} sessionId:{}", gatewayId, sessionId);
                return Mono.just(ResponseEntity.notFound().build());
            }

            // 反序列化 + 分发处理
            McpSchemaVO.JSONRPCMessage jsonrpcMessage = McpSchemaVO.deserializeJsonRpcMessage(messageBody);
            log.info("反序列化消息 jsonrpc:{}", jsonrpcMessage.jsonrpc());

            McpSchemaVO.JSONRPCResponse jsonrpcResponse = sessionMessageService.processHandlerMessage(jsonrpcMessage);

            // 处理结果通过 SSE Sink 推回客户端
            if (null != jsonrpcResponse) {
                String responseJson = OBJECT_MAPPER.writeValueAsString(jsonrpcResponse);
                session.getSink().tryEmitNext(ServerSentEvent.<String>builder()
                        .event("message")
                        .data(responseJson)
                        .build());
            }

            return Mono.just(ResponseEntity.accepted().build());
        } catch (Exception e) {
            log.error("处理 MCP SSE 消息失败 gatewayId:{} sessionId:{} messageBody:{}", gatewayId, sessionId, messageBody, e);
            return Mono.just(ResponseEntity.internalServerError().build());
        }
    }

}
