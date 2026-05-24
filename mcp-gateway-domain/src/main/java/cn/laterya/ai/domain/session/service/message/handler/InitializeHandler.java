package cn.laterya.ai.domain.session.service.message.handler;

import cn.laterya.ai.domain.session.adapter.repository.ISessionRepository;
import cn.laterya.ai.domain.session.model.McpSchemaVO;
import cn.laterya.ai.domain.session.model.valobj.McpGatewayConfigVO;
import cn.laterya.ai.domain.session.service.message.IRequestHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * MCP 协议握手处理器
 *
 * <p>响应 initialize 请求，从数据库查询网关配置，返回协议版本、能力和服务端信息。
 * 对照 Spring AI 源码：McpServerSession.handle → asyncInitializeRequestHandler
 */
@Slf4j
@Service("initializeHandler")
public class InitializeHandler implements IRequestHandler {

    @Resource
    private ISessionRepository repository;

    @Override
    public McpSchemaVO.JSONRPCResponse handle(String gatewayId, McpSchemaVO.JSONRPCRequest message) {
        log.info("消息处理服务-initialize gatewayId:{} request.params:{}", gatewayId, message.params());

        // 1. 解析客户端请求参数
        McpSchemaVO.InitializeRequest initializeRequest = McpSchemaVO.unmarshalFrom(message.params(), new TypeReference<>() {
        });

        // 2. 从数据库查询网关配置
        McpGatewayConfigVO config = repository.queryMcpGatewayConfigByGatewayId(gatewayId);
        if (null == config) {
            log.warn("网关配置不存在 gatewayId:{}", gatewayId);
            return new McpSchemaVO.JSONRPCResponse(McpSchemaVO.JSONRPC_VERSION, message.id(), null,
                    new McpSchemaVO.JSONRPCResponse.JSONRPCError(-32001, "网关配置不存在", null));
        }

        // 3. 组装 MCP Initialize 响应
        McpSchemaVO.InitializeResult result = new McpSchemaVO.InitializeResult(
                initializeRequest.protocolVersion(),
                new McpSchemaVO.ServerCapabilities(
                        new McpSchemaVO.ServerCapabilities.CompletionCapabilities(),
                        new HashMap<>(),
                        new McpSchemaVO.ServerCapabilities.LoggingCapabilities(),
                        new McpSchemaVO.ServerCapabilities.PromptCapabilities(true),
                        new McpSchemaVO.ServerCapabilities.ResourceCapabilities(false, true),
                        new McpSchemaVO.ServerCapabilities.ToolCapabilities(true)
                ),
                new McpSchemaVO.Implementation(config.getGatewayName(), config.getVersion()),
                config.getGatewayDesc()
        );

        // 4. 返回
        return new McpSchemaVO.JSONRPCResponse(McpSchemaVO.JSONRPC_VERSION, message.id(), result, null);
    }

}
