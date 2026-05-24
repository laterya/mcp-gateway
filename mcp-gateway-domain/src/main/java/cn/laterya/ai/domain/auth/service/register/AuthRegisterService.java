package cn.laterya.ai.domain.auth.service.register;

import cn.laterya.ai.domain.auth.adapter.repository.IAuthRepository;
import cn.laterya.ai.domain.auth.model.entity.RegisterCommandEntity;
import cn.laterya.ai.domain.auth.model.valobj.McpGatewayAuthVO;
import cn.laterya.ai.domain.auth.model.valobj.enums.AuthStatusEnum;
import cn.laterya.ai.domain.auth.service.IAuthRegisterService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

/**
 * 权限注册服务 —— 为网关生成 API Key 并持久化认证配置
 */
@Slf4j
@Service
public class AuthRegisterService implements IAuthRegisterService {

    @Resource
    private IAuthRepository repository;

    @Override
    public String register(RegisterCommandEntity commandEntity) {
        String apiKey = "gw-" + RandomStringUtils.randomAlphanumeric(48);

        McpGatewayAuthVO mcpGatewayAuthVO = McpGatewayAuthVO.builder()
                .gatewayId(commandEntity.getGatewayId())
                .apiKey(apiKey)
                .rateLimit(commandEntity.getRateLimit())
                .expireTime(commandEntity.getExpireTime())
                .status(AuthStatusEnum.AuthConfig.ENABLE)
                .build();

        repository.insert(mcpGatewayAuthVO);

        return apiKey;
    }

}
