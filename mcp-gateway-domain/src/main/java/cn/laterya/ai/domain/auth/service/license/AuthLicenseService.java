package cn.laterya.ai.domain.auth.service.license;

import cn.laterya.ai.domain.auth.adapter.repository.IAuthRepository;
import cn.laterya.ai.domain.auth.model.entity.LicenseCommandEntity;
import cn.laterya.ai.domain.auth.model.valobj.McpGatewayAuthVO;
import cn.laterya.ai.domain.auth.model.valobj.enums.AuthStatusEnum;
import cn.laterya.ai.domain.auth.service.IAuthLicenseService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 权限证书校验服务
 *
 * 校验链：网关校验模式 → 认证配置是否存在 → 启用状态 → 过期时间
 * 任一环节不通过即返回 false，短路返回避免无效查询
 */
@Slf4j
@Service
public class AuthLicenseService implements IAuthLicenseService {

    @Resource
    private IAuthRepository repository;

    @Override
    public boolean checkLicense(LicenseCommandEntity commandEntity) {
        // 网关配置为"不校验"时直接放行，跳过后续认证查询
        AuthStatusEnum.GatewayConfig gatewayAuthStatus = repository.queryGatewayAuthStatus(commandEntity.getGatewayId());
        if (AuthStatusEnum.GatewayConfig.NOT_VERIFIED.equals(gatewayAuthStatus)) return true;

        McpGatewayAuthVO mcpGatewayAuthVO = repository.queryEffectiveGatewayAuthInfo(commandEntity);
        if (null == mcpGatewayAuthVO) return false;

        if (AuthStatusEnum.AuthConfig.DISABLE.equals(mcpGatewayAuthVO.getStatus())) {
            return false;
        }

        // 未设置过期时间表示永久有效
        LocalDateTime expireTime = mcpGatewayAuthVO.getExpireTime();
        if (null == expireTime) return true;

        boolean isValid = LocalDateTime.now().isBefore(expireTime);
        if (!isValid) {
            log.warn("apiKey 权限校验，expireTime 已过期。gatewayId:{} apiKey:{}", commandEntity.getGatewayId(), commandEntity.getApiKey());
        }
        return isValid;
    }

}
