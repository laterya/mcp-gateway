package cn.laterya.ai.cases.admin.manage;

import cn.laterya.ai.cases.admin.IAdminManageService;
import cn.laterya.ai.domain.admin.model.entity.*;
import cn.laterya.ai.domain.admin.service.IAdminService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AdminManageService implements IAdminManageService {

    @Resource
    private IAdminService adminService;

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
