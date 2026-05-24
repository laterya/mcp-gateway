package cn.laterya.ai.infrastructure.adapter.repository;

import cn.laterya.ai.domain.auth.adapter.repository.IAuthRepository;
import cn.laterya.ai.domain.auth.model.entity.LicenseCommandEntity;
import cn.laterya.ai.domain.auth.model.valobj.McpGatewayAuthVO;
import cn.laterya.ai.domain.auth.model.valobj.enums.AuthStatusEnum;
import cn.laterya.ai.infrastructure.dao.IMcpGatewayAuthDao;
import cn.laterya.ai.infrastructure.dao.IMcpGatewayDao;
import cn.laterya.ai.infrastructure.dao.po.McpGatewayAuthPO;
import cn.laterya.ai.infrastructure.dao.po.McpGatewayPO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * 鉴权仓储服务 —— 实现 domain 层 IAuthRepository 端口，完成 PO↔VO 转换
 */
@Slf4j
@Repository
public class AuthRepository implements IAuthRepository {

    @Resource
    private IMcpGatewayAuthDao mcpGatewayAuthDao;

    @Resource
    private IMcpGatewayDao mcpGatewayDao;

    @Override
    public int queryEffectiveGatewayAuthCount(String gatewayId) {
        return mcpGatewayAuthDao.queryEffectiveGatewayAuthCount(gatewayId);
    }

    @Override
    public McpGatewayAuthVO queryEffectiveGatewayAuthInfo(LicenseCommandEntity commandEntity) {
        McpGatewayAuthPO poReq = new McpGatewayAuthPO();
        poReq.setGatewayId(commandEntity.getGatewayId());
        poReq.setApiKey(commandEntity.getApiKey());

        McpGatewayAuthPO po = mcpGatewayAuthDao.queryByGatewayIdAndApiKey(poReq);
        if (null == po) return null;

        return McpGatewayAuthVO.builder()
                .gatewayId(po.getGatewayId())
                .apiKey(po.getApiKey())
                .rateLimit(po.getRateLimit())
                .expireTime(po.getExpireTime())
                .status(AuthStatusEnum.AuthConfig.get(po.getStatus()))
                .build();
    }

    @Override
    public void insert(McpGatewayAuthVO mcpGatewayAuthVO) {
        McpGatewayAuthPO po = McpGatewayAuthPO.builder()
                .gatewayId(mcpGatewayAuthVO.getGatewayId())
                .apiKey(mcpGatewayAuthVO.getApiKey())
                .rateLimit(mcpGatewayAuthVO.getRateLimit())
                .expireTime(mcpGatewayAuthVO.getExpireTime())
                .status(mcpGatewayAuthVO.getStatus().getCode())
                .build();
        mcpGatewayAuthDao.insert(po);
    }

    @Override
    public AuthStatusEnum.GatewayConfig queryGatewayAuthStatus(String gatewayId) {
        // 鉴权模式配置在 mcp_gateway 表的 auth 字段，而非 mcp_gateway_auth 表
        McpGatewayPO mcpGatewayPO = mcpGatewayDao.queryByGatewayId(gatewayId);
        return AuthStatusEnum.GatewayConfig.get(mcpGatewayPO.getAuth());
    }

}
