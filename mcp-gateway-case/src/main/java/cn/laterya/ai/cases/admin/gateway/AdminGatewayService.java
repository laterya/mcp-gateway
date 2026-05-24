package cn.laterya.ai.cases.admin.gateway;

import cn.laterya.ai.cases.admin.IAdminGatewayService;
import cn.laterya.ai.domain.gateway.model.entity.GatewayConfigCommandEntity;
import cn.laterya.ai.domain.gateway.model.entity.GatewayToolConfigCommandEntity;
import cn.laterya.ai.domain.gateway.service.IGatewayConfigService;
import cn.laterya.ai.domain.gateway.service.IGatewayToolConfigService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdminGatewayService implements IAdminGatewayService {

    @Resource
    private IGatewayConfigService gatewayConfigService;
    @Resource
    private IGatewayToolConfigService gatewayToolConfigService;

    @Override
    public void saveGatewayConfig(GatewayConfigCommandEntity commandEntity) {
        gatewayConfigService.saveGatewayConfig(commandEntity);
    }

    @Override
    public void saveGatewayToolConfig(GatewayToolConfigCommandEntity commandEntity) {
        gatewayToolConfigService.saveGatewayToolConfig(commandEntity);
    }

}
