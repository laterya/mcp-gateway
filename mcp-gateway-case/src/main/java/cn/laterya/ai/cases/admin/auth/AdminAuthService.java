package cn.laterya.ai.cases.admin.auth;

import cn.laterya.ai.cases.admin.IAdminAuthService;
import cn.laterya.ai.domain.auth.model.entity.RegisterCommandEntity;
import cn.laterya.ai.domain.auth.service.IAuthRegisterService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdminAuthService implements IAdminAuthService {

    @Resource
    private IAuthRegisterService authRegisterService;

    @Override
    public void saveGatewayAuth(RegisterCommandEntity commandEntity) {
        authRegisterService.register(commandEntity);
    }

}
