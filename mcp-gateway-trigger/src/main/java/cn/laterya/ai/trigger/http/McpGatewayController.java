package cn.laterya.ai.trigger.http;

import cn.laterya.ai.api.IMcpGatewayService;
import cn.laterya.ai.cases.mcp.shared.message.IMcpMessageService;
import cn.laterya.ai.cases.mcp.sse.IMcpSseSessionService;
import cn.laterya.ai.domain.session.model.entity.HandleMessageCommandEntity;
import cn.laterya.ai.types.enums.ResponseCode;
import cn.laterya.ai.types.exception.AppException;
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
 * MCP 网关控制器 — SSE 传输（trigger 层）
 *
 * <p>SSE 双通道设计：
 * <ul>
 *   <li>GET /{gatewayId}/mcp/sse?api_key=xxx → 建立 SSE 流（服务端推送通道）</li>
 *   <li>POST /{gatewayId}/mcp/sse?sessionId=xxx&api_key=xxx → 客户端发送消息（请求通道）</li>
 * </ul>
 */
@Slf4j
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@RequestMapping("/")
public class McpGatewayController implements IMcpGatewayService {

    @Resource(name = "sseMcpSessionService")
    private IMcpSseSessionService mcpSessionService;

    @Resource(name = "mcpMessageService")
    private IMcpMessageService mcpMessageService;

    @Override
    @GetMapping(value = "{gatewayId}/mcp/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> establishSSEConnection(@PathVariable("gatewayId") String gatewayId,
                                                                  @RequestParam(value = "api_key", required = false) String apiKey) {
        try {
            log.info("建立 MCP SSE 连接 gatewayId:{} apiKey:{}", gatewayId, apiKey);

            if (!StringUtils.hasText(gatewayId)) {
                log.info("非法参数，gatewayId 为空");
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }

            return mcpSessionService.createMcpSession(gatewayId, apiKey);
        } catch (Exception e) {
            log.error("建立 MCP SSE 连接失败 gatewayId:{}", gatewayId, e);
            throw e;
        }
    }

    @Override
    @PostMapping(value = "{gatewayId}/mcp/sse", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Void>> handleMessage(@PathVariable("gatewayId") String gatewayId,
                                                     @RequestParam String sessionId,
                                                     @RequestParam(value = "api_key", required = false) String apiKey,
                                                     @RequestBody String messageBody) {
        try {
            log.info("处理 MCP SSE 消息 gatewayId:{} apiKey:{} sessionId:{} messageBody:{}", gatewayId, apiKey, sessionId, messageBody);

            HandleMessageCommandEntity commandEntity = new HandleMessageCommandEntity(gatewayId, apiKey, sessionId, messageBody);
            ResponseEntity<Void> responseEntity = mcpMessageService.handleMessage(commandEntity);
            return Mono.just(responseEntity);
        } catch (Exception e) {
            log.error("处理 MCP SSE 消息失败 gatewayId:{} sessionId:{} messageBody:{}", gatewayId, sessionId, messageBody, e);
            return Mono.just(ResponseEntity.internalServerError().build());
        }
    }

}
