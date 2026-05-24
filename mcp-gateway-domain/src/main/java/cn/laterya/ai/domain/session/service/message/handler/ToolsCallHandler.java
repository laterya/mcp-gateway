package cn.laterya.ai.domain.session.service.message.handler;

import cn.laterya.ai.domain.session.adapter.port.ISessionPort;
import cn.laterya.ai.domain.session.adapter.repository.ISessionRepository;
import cn.laterya.ai.domain.session.model.McpSchemaVO;
import cn.laterya.ai.domain.session.model.valobj.McpToolProtocolConfigVO;
import cn.laterya.ai.domain.session.service.message.IRequestHandler;
import cn.laterya.ai.types.enums.McpErrorCodes;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 工具调用处理器
 *
 * <p>接收 AI 客户端的 tools/call 请求，通过 gatewayId + toolName 精确查询协议配置，
 * 调用后端 HTTP 接口，将结果包装为 MCP 协议响应。
 */
@Slf4j
@Service("toolsCallHandler")
public class ToolsCallHandler implements IRequestHandler {

    @Resource
    private ISessionRepository repository;

    @Resource
    private ISessionPort port;

    @Override
    public McpSchemaVO.JSONRPCResponse handle(String gatewayId, McpSchemaVO.JSONRPCRequest message) {
        try {
            // 1. 解析调用参数
            McpSchemaVO.CallToolRequest callToolRequest = McpSchemaVO.unmarshalFrom(message.params(), new TypeReference<>() {});
            String toolName = callToolRequest.name();
            Object arguments = callToolRequest.arguments();

            // 2. 按 gatewayId + toolName 查询协议配置
            McpToolProtocolConfigVO protocolConfig = repository.queryMcpGatewayProtocolConfig(gatewayId, toolName);
            if (null == protocolConfig || null == protocolConfig.getHttpConfig()) {
                return new McpSchemaVO.JSONRPCResponse(McpSchemaVO.JSONRPC_VERSION, message.id(), null,
                        new McpSchemaVO.JSONRPCResponse.JSONRPCError(
                                McpErrorCodes.TOOL_NOT_FOUND, "工具协议配置不存在: " + toolName, null));
            }

            // 3. 调用后端接口
            Object result = port.toolCall(protocolConfig.getHttpConfig(), arguments);

            // 4. 包装 MCP 响应
            return new McpSchemaVO.JSONRPCResponse(McpSchemaVO.JSONRPC_VERSION, message.id(), Map.of(
                    "content", new Object[]{
                            Map.of("type", "text", "text", result)
                    },
                    "isError", "false"
            ), null);

        } catch (Exception e) {
            log.error("工具调用异常 gatewayId:{}", gatewayId, e);
            return new McpSchemaVO.JSONRPCResponse(McpSchemaVO.JSONRPC_VERSION, message.id(), null,
                    new McpSchemaVO.JSONRPCResponse.JSONRPCError(
                            McpErrorCodes.TOOL_EXECUTION_FAILED, e.getMessage(), null));
        }
    }

}
