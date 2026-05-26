package cn.laterya.ai.cases.admin.auth;

import cn.laterya.ai.cases.admin.IAdminAuthService;
import cn.laterya.ai.domain.auth.adapter.repository.IAuthRepository;
import cn.laterya.ai.domain.auth.model.entity.RegisterCommandEntity;
import cn.laterya.ai.domain.auth.service.IAuthRegisterService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class AdminAuthService implements IAdminAuthService {

    @Resource private IAuthRegisterService authRegisterService;
    @Resource private IAuthRepository authRepository;

    @Override
    public void saveGatewayAuth(RegisterCommandEntity commandEntity) { authRegisterService.register(commandEntity); }

    @Override
    public void deleteGatewayAuth(String gatewayId) { authRepository.deleteByGatewayId(gatewayId); }

    @Override
    public void updateGatewayAuth(String gatewayId, Integer rateLimit, String expireTime) {
        LocalDateTime exp = (expireTime != null && !expireTime.isEmpty()) ? LocalDateTime.parse(expireTime) : null;
        authRepository.updateAuth(gatewayId, rateLimit, exp);
    }
}
