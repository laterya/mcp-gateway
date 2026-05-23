package cn.laterya.ai.domain.session.service.message.handler;

import cn.laterya.ai.domain.session.model.McpSchemaVO;
import cn.laterya.ai.domain.session.service.message.IRequestHandler;
import cn.laterya.ai.types.enums.McpErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 工具调用处理器
 *
 * <p>接收 AI 客户端通过 MCP 协议下发的工具调用请求，解析出：
 * <ul>
 *   <li>params.name → 要调用的工具名</li>
 *   <li>params.arguments → 工具入参</li>
 * </ul>
 *
 * <p>当前硬编码处理 toUpperCase 方法。后续会替换为动态路由：
 * 可以转发到 HTTP 接口、RPC、MQ，甚至 RS232 串口通信控制硬件。
 */
@Slf4j
@Service("toolsCallHandler")
public class ToolsCallHandler implements IRequestHandler {

    @Override
    @SuppressWarnings("unchecked")
    public McpSchemaVO.JSONRPCResponse handle(McpSchemaVO.JSONRPCRequest message) {
        Object params = message.params();

        if (!(params instanceof Map)) {
            return new McpSchemaVO.JSONRPCResponse("2.0", message.id(), null,
                    new McpSchemaVO.JSONRPCResponse.JSONRPCError(
                            McpErrorCodes.INVALID_PARAMS, "无效参数 - 方法参数格式不正确", null));
        }

        Map<String, Object> paramsMap = (Map<String, Object>) params;
        String toolName = (String) paramsMap.get("name");
        Map<String, Object> arguments = (Map<String, Object>) paramsMap.get("arguments");

        if ("toUpperCase".equals(toolName)) {
            String word = arguments.get("word").toString();
            return new McpSchemaVO.JSONRPCResponse("2.0", message.id(), Map.of(
                    "content", new Object[]{
                            Map.of("type", "text", "text", word.toUpperCase())
                    }
            ), null);
        }

        return new McpSchemaVO.JSONRPCResponse("2.0", message.id(), null,
                new McpSchemaVO.JSONRPCResponse.JSONRPCError(
                        McpErrorCodes.METHOD_NOT_FOUND, "方法未找到 - 方法不存在或不可用", null));
    }

}
