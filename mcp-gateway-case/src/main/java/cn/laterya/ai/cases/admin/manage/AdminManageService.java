package cn.laterya.ai.cases.admin.manage;

import cn.laterya.ai.cases.admin.IAdminManageService;
import cn.laterya.ai.domain.admin.model.entity.GatewayConfigEntity;
import cn.laterya.ai.domain.admin.service.IAdminService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@org.springframework.stereotype.Service
public class AdminManageService implements IAdminManageService {

    @Resource
    private IAdminService adminService;

    @Override
    public List<GatewayConfigEntity> queryGatewayConfigList() {
        return adminService.queryGatewayConfigList();
    }

}
