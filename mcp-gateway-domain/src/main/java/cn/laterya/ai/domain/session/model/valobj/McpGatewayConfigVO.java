package cn.laterya.ai.domain.session.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 网关配置值对象
 *
 * <p>由 mcp_gateway 表数据组装，纯网关级别信息。
 * 供 InitializeHandler 使用，填充 MCP 协议响应。
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class McpGatewayConfigVO {

    private String gatewayId;
    private String gatewayName;
    private String gatewayDesc;
    private String version;

}
