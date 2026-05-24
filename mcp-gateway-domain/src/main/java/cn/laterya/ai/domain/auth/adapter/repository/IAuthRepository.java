package cn.laterya.ai.domain.auth.adapter.repository;

import cn.laterya.ai.domain.auth.model.entity.LicenseCommandEntity;
import cn.laterya.ai.domain.auth.model.valobj.McpGatewayAuthVO;
import cn.laterya.ai.domain.auth.model.valobj.enums.AuthStatusEnum;

/**
 * 鉴权仓储端口 —— domain 层定义，由 infrastructure 层实现
 */
public interface IAuthRepository {

    /** 查询网关下有效的认证记录数 */
    int queryEffectiveGatewayAuthCount(String gatewayId);

    /** 按 gatewayId + apiKey 查询有效的认证配置 */
    McpGatewayAuthVO queryEffectiveGatewayAuthInfo(LicenseCommandEntity commandEntity);

    /** 新增认证记录 */
    void insert(McpGatewayAuthVO mcpGatewayAuthVO);

    /** 查询网关的鉴权模式（不校验 / 强校验） */
    AuthStatusEnum.GatewayConfig queryGatewayAuthStatus(String gatewayId);

}
