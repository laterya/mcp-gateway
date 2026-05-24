package cn.laterya.ai.domain.admin.service.impl;

import cn.laterya.ai.domain.admin.adapter.repository.IAdminRepository;
import cn.laterya.ai.domain.admin.model.entity.*;
import cn.laterya.ai.domain.admin.service.IAdminService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AdminService implements IAdminService {

    @Resource
    private IAdminRepository adminRepository;

    @Override
    public List<GatewayConfigEntity> queryGatewayConfigList() { return adminRepository.queryGatewayConfigList(); }

    @Override
    public GatewayConfigPageEntity queryGatewayConfigPage(GatewayConfigQueryEntity q) { return adminRepository.queryGatewayConfigPage(q); }

    @Override
    public List<GatewayToolConfigEntity> queryGatewayToolList() { return adminRepository.queryGatewayToolList(); }

    @Override
    public GatewayToolPageEntity queryGatewayToolPage(GatewayToolQueryEntity q) { return adminRepository.queryGatewayToolPage(q); }

    @Override
    public List<GatewayToolConfigEntity> queryGatewayToolListByGatewayId(String gatewayId) { return adminRepository.queryGatewayToolListByGatewayId(gatewayId); }

    @Override
    public List<GatewayProtocolConfigEntity> queryGatewayProtocolList() { return adminRepository.queryGatewayProtocolList(); }

    @Override
    public GatewayProtocolPageEntity queryGatewayProtocolPage(GatewayProtocolQueryEntity q) { return adminRepository.queryGatewayProtocolPage(q); }

    @Override
    public List<GatewayProtocolConfigEntity> queryGatewayProtocolListByGatewayId(String gatewayId) {
        List<GatewayToolConfigEntity> tools = adminRepository.queryGatewayToolListByGatewayId(gatewayId);
        if (tools == null || tools.isEmpty()) return Collections.emptyList();
        List<Long> protocolIds = tools.stream().map(GatewayToolConfigEntity::getProtocolId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (protocolIds.isEmpty()) return Collections.emptyList();
        return adminRepository.queryGatewayProtocolListByProtocolIds(protocolIds);
    }

    @Override
    public List<GatewayAuthConfigEntity> queryGatewayAuthList() { return adminRepository.queryGatewayAuthList(); }

    @Override
    public GatewayAuthPageEntity queryGatewayAuthPage(GatewayAuthQueryEntity q) { return adminRepository.queryGatewayAuthPage(q); }

}
