package cn.laterya.ai.domain.session.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 工具配置值对象
 *
 * <p>由 mcp_gateway_tool 组装，嵌套 McpToolProtocolConfigVO（协议 + 字段映射）。
 * 用于 ToolsListHandler（遍历工具列表）和 ToolsCallHandler（按名称查找工具）。
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class McpToolConfigVO {

    private Long toolId;
    private String toolName;
    private String toolDescription;
    private McpToolProtocolConfigVO mcpToolProtocolConfigVO;

}
