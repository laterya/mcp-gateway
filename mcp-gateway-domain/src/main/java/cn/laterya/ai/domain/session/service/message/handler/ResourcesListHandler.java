package cn.laterya.ai.domain.session.service.message.handler;

import cn.laterya.ai.domain.session.model.McpSchemaVO;
import cn.laterya.ai.domain.session.service.message.IRequestHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 资源列表处理器
 *
 * <p>返回空资源列表。MCP 协议中 resources 用于暴露服务端数据（文件、数据库记录等），
 * 当前网关不提供资源功能，返回空数组即可。
 */
@Slf4j
@Service("resourcesListHandler")
public class ResourcesListHandler implements IRequestHandler {

    @Override
    public McpSchemaVO.JSONRPCResponse handle(McpSchemaVO.JSONRPCRequest message) {
        return new McpSchemaVO.JSONRPCResponse("2.0", message.id(), Map.of(
                "resources", new Object[]{}
        ), null);
    }

}
