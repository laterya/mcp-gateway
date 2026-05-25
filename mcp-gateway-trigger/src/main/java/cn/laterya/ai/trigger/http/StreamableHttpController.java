package cn.laterya.ai.trigger.http;

import cn.laterya.ai.cases.mcp.IMcpStreamableSessionService;
import cn.laterya.ai.cases.mcp.streamable.StreamableChainContext;
import cn.laterya.ai.domain.session.model.McpSchemaVO;
import cn.laterya.ai.types.enums.McpErrorCodes;
import cn.laterya.ai.types.exception.AppException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * Streamable HTTP 控制器 —— trigger 层
 *
 * <p>MCP Streamable HTTP 传输端点（2025-03-26 规范）：
 * <ul>
 *   <li>POST /{gatewayId}/mcp → 处理所有 JSON-RPC 消息</li>
 *   <li>鉴权通过 Authorization: Bearer {api_key} 头</li>
 *   <li>initialize 响应携带 Mcp-Session-Id 头</li>
 * </ul>
 */
@Slf4j
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@RequestMapping("/")
public class StreamableHttpController {

    private static final String BEARER_PREFIX = "Bearer ";

    @Resource
    private IMcpStreamableSessionService mcpStreamableSessionService;

    @Resource
    private ObjectMapper objectMapper;

    @PostMapping(value = "{gatewayId}/mcp", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> handleMessage(
            @PathVariable("gatewayId") String gatewayId,
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestHeader(value = "Mcp-Session-Id", required = false) String mcpSessionId,
            @RequestBody String messageBody) {
        try {
            log.info("Streamable HTTP 请求 gatewayId:{} sessionId:{}", gatewayId, mcpSessionId);

            if (!StringUtils.hasText(gatewayId)) {
                return ResponseEntity.badRequest().build();
            }

            String apiKey = extractApiKey(authorization);

            StreamableChainContext context = mcpStreamableSessionService.handleRequest(
                    gatewayId, apiKey, mcpSessionId, messageBody);

            // 构建响应
            McpSchemaVO.JSONRPCResponse response = context.getResponse();
            if (response == null) {
                return ResponseEntity.ok().build();
            }

            String responseBody = objectMapper.writeValueAsString(response);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_JSON);

            // initialize 时返回 Mcp-Session-Id 头
            if (context.getSessionId() != null && mcpSessionId == null) {
                responseHeaders.set("Mcp-Session-Id", context.getSessionId());
            }

            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body(responseBody);

        } catch (AppException e) {
            log.warn("Streamable HTTP 业务异常 gatewayId:{} code:{}", gatewayId, e.getCode());
            McpSchemaVO.JSONRPCResponse errorResponse = new McpSchemaVO.JSONRPCResponse(
                    McpSchemaVO.JSONRPC_VERSION, null, null,
                    new McpSchemaVO.JSONRPCResponse.JSONRPCError(
                            e.getIntCode(), e.getMessage(), null));
            try {
                return ResponseEntity.status(401)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(errorResponse));
            } catch (Exception ex) {
                return ResponseEntity.status(401).build();
            }
        } catch (Exception e) {
            log.error("Streamable HTTP 处理失败 gatewayId:{}", gatewayId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private String extractApiKey(String authorization) {
        if (authorization != null && authorization.startsWith(BEARER_PREFIX)) {
            return authorization.substring(BEARER_PREFIX.length()).trim();
        }
        return null;
    }

}
