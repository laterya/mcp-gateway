package cn.laterya.ai.cases.admin.orchestration;

import cn.laterya.ai.cases.admin.IAdminOrchestrationService;
import cn.laterya.ai.domain.admin.model.entity.*;
import cn.laterya.ai.domain.admin.service.IAdminService;
import cn.laterya.ai.domain.auth.adapter.repository.IAuthRepository;
import cn.laterya.ai.domain.auth.model.entity.RegisterCommandEntity;
import cn.laterya.ai.domain.auth.service.IAuthRegisterService;
import cn.laterya.ai.domain.gateway.model.entity.GatewayConfigCommandEntity;
import cn.laterya.ai.domain.gateway.model.entity.GatewayToolConfigCommandEntity;
import cn.laterya.ai.domain.gateway.service.IGatewayConfigService;
import cn.laterya.ai.domain.gateway.service.IGatewayToolConfigService;
import cn.laterya.ai.domain.protocol.model.entity.StorageCommandEntity;
import cn.laterya.ai.domain.protocol.service.IProtocolStorage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class AdminOrchestrationService implements IAdminOrchestrationService {

    @Resource private IGatewayConfigService gatewayConfigService;
    @Resource private IGatewayToolConfigService gatewayToolConfigService;
    @Resource private IProtocolStorage protocolStorage;
    @Resource private IAuthRegisterService authRegisterService;
    @Resource private IAuthRepository authRepository;
    @Resource private IAdminService adminService;

    // ===== Gateway 配置 =====

    @Override
    public void saveGatewayConfig(GatewayConfigCommandEntity commandEntity) {
        gatewayConfigService.saveGatewayConfig(commandEntity);
    }

    @Override
    public void saveGatewayToolConfig(GatewayToolConfigCommandEntity commandEntity) {
        gatewayToolConfigService.saveGatewayToolConfig(commandEntity);
    }

    // ===== Protocol =====

    @Override
    public void saveGatewayProtocol(StorageCommandEntity commandEntity) {
        protocolStorage.doStorage(commandEntity);
    }

    @Override
    public void deleteGatewayProtocol(Long protocolId) {
        log.info("删除协议 protocolId:{}", protocolId);
        protocolStorage.deleteByProtocolId(protocolId);
    }

    // ===== Auth =====

    @Override
    public void saveGatewayAuth(RegisterCommandEntity commandEntity) {
        authRegisterService.register(commandEntity);
    }

    @Override
    public void deleteGatewayAuth(String gatewayId) {
        log.info("删除网关鉴权 gatewayId:{}", gatewayId);
    }

    @Override
    public void updateGatewayAuth(String gatewayId, Integer rateLimit, String expireTime) {
        LocalDateTime exp = (expireTime != null && !expireTime.isEmpty()) ? LocalDateTime.parse(expireTime) : null;
        authRepository.updateAuth(gatewayId, rateLimit, exp);
    }

    // ===== 查询 =====

    @Override
    public List<GatewayConfigEntity> queryGatewayConfigList() { return adminService.queryGatewayConfigList(); }

    @Override
    public GatewayConfigPageEntity queryGatewayConfigPage(GatewayConfigQueryEntity q) { return adminService.queryGatewayConfigPage(q); }

    @Override
    public List<GatewayToolConfigEntity> queryGatewayToolList() { return adminService.queryGatewayToolList(); }

    @Override
    public GatewayToolPageEntity queryGatewayToolPage(GatewayToolQueryEntity q) { return adminService.queryGatewayToolPage(q); }

    @Override
    public List<GatewayToolConfigEntity> queryGatewayToolListByGatewayId(String gatewayId) { return adminService.queryGatewayToolListByGatewayId(gatewayId); }

    @Override
    public List<GatewayProtocolConfigEntity> queryGatewayProtocolList() { return adminService.queryGatewayProtocolList(); }

    @Override
    public GatewayProtocolPageEntity queryGatewayProtocolPage(GatewayProtocolQueryEntity q) { return adminService.queryGatewayProtocolPage(q); }

    @Override
    public List<GatewayProtocolConfigEntity> queryGatewayProtocolListByGatewayId(String gatewayId) { return adminService.queryGatewayProtocolListByGatewayId(gatewayId); }

    @Override
    public List<GatewayAuthConfigEntity> queryGatewayAuthList() { return adminService.queryGatewayAuthList(); }

    @Override
    public GatewayAuthPageEntity queryGatewayAuthPage(GatewayAuthQueryEntity q) { return adminService.queryGatewayAuthPage(q); }

}
