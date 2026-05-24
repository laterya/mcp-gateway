package cn.laterya.ai.domain.session.service.message.handler;

import cn.laterya.ai.domain.session.adapter.repository.ISessionRepository;
import cn.laterya.ai.domain.session.model.McpSchemaVO;
import cn.laterya.ai.domain.session.model.valobj.McpToolConfigVO;
import cn.laterya.ai.domain.session.model.valobj.McpToolProtocolConfigVO;
import cn.laterya.ai.domain.session.service.message.IRequestHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 工具列表处理器
 *
 * <p>从数据库查询工具配置列表（每个工具嵌套协议映射），递归组装为 MCP tools/list 协议结构。
 */
@Slf4j
@Service("toolsListHandler")
public class ToolsListHandler implements IRequestHandler {

    @Resource
    private ISessionRepository repository;

    @Override
    public McpSchemaVO.JSONRPCResponse handle(String gatewayId, McpSchemaVO.JSONRPCRequest message) {
        log.info("消息处理服务-tools/list gatewayId:{}", gatewayId);

        List<McpToolConfigVO> toolConfigs = repository.queryMcpGatewayToolConfigListByGatewayId(gatewayId);
        List<McpSchemaVO.Tool> tools = buildTools(toolConfigs);

        return new McpSchemaVO.JSONRPCResponse(McpSchemaVO.JSONRPC_VERSION, message.id(), Map.of(
                "tools", tools
        ), null);
    }

    private List<McpSchemaVO.Tool> buildTools(List<McpToolConfigVO> toolConfigs) {
        List<McpSchemaVO.Tool> tools = new ArrayList<>();

        for (McpToolConfigVO toolConfig : toolConfigs) {
            List<McpToolProtocolConfigVO.ProtocolMapping> mappings =
                    toolConfig.getMcpToolProtocolConfigVO().getRequestProtocolMappings();

            mappings.sort(Comparator.comparingInt(m -> m.getSortOrder() != null ? m.getSortOrder() : 0));

            // 分离根节点和子节点
            Map<String, List<McpToolProtocolConfigVO.ProtocolMapping>> childrenMap = new HashMap<>();
            List<McpToolProtocolConfigVO.ProtocolMapping> roots = new ArrayList<>();

            for (McpToolProtocolConfigVO.ProtocolMapping mapping : mappings) {
                if (mapping.getParentPath() == null) {
                    roots.add(mapping);
                } else {
                    childrenMap.computeIfAbsent(mapping.getParentPath(), k -> new ArrayList<>()).add(mapping);
                }
            }

            roots.sort(Comparator.comparingInt(m -> m.getSortOrder() != null ? m.getSortOrder() : 0));

            // 构建 inputSchema
            Map<String, Object> properties = new LinkedHashMap<>();
            List<String> required = new ArrayList<>();

            for (McpToolProtocolConfigVO.ProtocolMapping root : roots) {
                properties.put(root.getFieldName(), buildProperty(root, childrenMap));
                if (Integer.valueOf(1).equals(root.getIsRequired())) {
                    required.add(root.getFieldName());
                }
            }

            String type = roots.size() == 1 ? roots.get(0).getMcpType() : "object";

            McpSchemaVO.JsonSchema inputSchema = new McpSchemaVO.JsonSchema(
                    type, properties, required.isEmpty() ? null : required, false, null, null
            );

            tools.add(new McpSchemaVO.Tool(
                    toolConfig.getToolName(), toolConfig.getToolDescription(), inputSchema));
        }
        return tools;
    }

    private Map<String, Object> buildProperty(McpToolProtocolConfigVO.ProtocolMapping current,
                                               Map<String, List<McpToolProtocolConfigVO.ProtocolMapping>> childrenMap) {
        Map<String, Object> property = new LinkedHashMap<>();
        property.put("type", current.getMcpType());
        if (current.getMcpDesc() != null) {
            property.put("description", current.getMcpDesc());
        }

        List<McpToolProtocolConfigVO.ProtocolMapping> children = childrenMap.get(current.getMcpPath());
        if (children != null && !children.isEmpty()) {
            Map<String, Object> props = new LinkedHashMap<>();
            List<String> reqs = new ArrayList<>();

            children.sort(Comparator.comparingInt(m -> m.getSortOrder() != null ? m.getSortOrder() : 0));

            for (McpToolProtocolConfigVO.ProtocolMapping child : children) {
                props.put(child.getFieldName(), buildProperty(child, childrenMap));
                if (Integer.valueOf(1).equals(child.getIsRequired())) {
                    reqs.add(child.getFieldName());
                }
            }

            property.put("properties", props);
            if (!reqs.isEmpty()) {
                property.put("required", reqs);
            }
        }

        return property;
    }

}
