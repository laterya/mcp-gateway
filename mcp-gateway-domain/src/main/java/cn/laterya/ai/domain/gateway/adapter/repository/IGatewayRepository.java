package cn.laterya.ai.domain.gateway.adapter.repository;

import cn.laterya.ai.domain.gateway.model.entity.GatewayConfigCommandEntity;
import cn.laterya.ai.domain.gateway.model.entity.GatewayToolConfigCommandEntity;

/**
 * 网关仓储端口（DDD Port）
 *
 * <p>domain 层定义，infrastructure 层实现。CRUD 操作分别对应 mcp_gateway 和 mcp_gateway_tool 两张表。
 */
public interface IGatewayRepository {

    void saveGatewayConfig(GatewayConfigCommandEntity commandEntity);

    void updateGatewayAuthStatus(GatewayConfigCommandEntity commandEntity);

    void saveGatewayToolConfig(GatewayToolConfigCommandEntity commandEntity);

    void updateGatewayToolProtocol(GatewayToolConfigCommandEntity commandEntity);

}
