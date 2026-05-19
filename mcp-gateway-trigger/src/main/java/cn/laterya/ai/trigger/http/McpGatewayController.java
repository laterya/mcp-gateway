package cn.laterya.ai.trigger.http;

import cn.laterya.ai.api.IMcpGatewayService;
import cn.laterya.ai.cases.mcp.IMcpSessionService;
import cn.laterya.ai.types.enums.ResponseCode;
import cn.laterya.ai.types.exception.AppException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * MCP 网关控制器 —— trigger 层
 *
 * <p>职责（只做接口封装，不含业务逻辑）：
 * 1. 日志打印
 * 2. 参数校验
 * 3. 调用 case 层
 * 4. 异常处理 & 结果封装
 *
 * <p>示例地址：http://localhost:8090/api-gateway/test10001/mcp/sse
 */
@Slf4j
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@RequestMapping("/")
public class McpGatewayController implements IMcpGatewayService {

    @Resource
    private IMcpSessionService mcpSessionService;

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

}
