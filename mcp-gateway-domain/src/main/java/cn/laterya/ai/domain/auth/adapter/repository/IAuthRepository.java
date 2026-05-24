package cn.laterya.ai.domain.auth.adapter.repository;

import cn.laterya.ai.domain.auth.model.entity.LicenseCommandEntity;
import cn.laterya.ai.domain.auth.model.valobj.McpGatewayAuthVO;
import cn.laterya.ai.domain.auth.model.valobj.enums.AuthStatusEnum;

public interface IAuthRepository {
    int queryEffectiveGatewayAuthCount(String gatewayId);
    McpGatewayAuthVO queryEffectiveGatewayAuthInfo(LicenseCommandEntity commandEntity);
    void insert(McpGatewayAuthVO mcpGatewayAuthVO);
    AuthStatusEnum.GatewayConfig queryGatewayAuthStatus(String gatewayId);
    void updateAuth(String gatewayId, Integer rateLimit, java.time.LocalDateTime expireTime);
}
