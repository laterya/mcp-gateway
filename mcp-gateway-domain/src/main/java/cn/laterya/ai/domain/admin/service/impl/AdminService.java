package cn.laterya.ai.domain.admin.service.impl;

import cn.laterya.ai.domain.admin.adapter.repository.IAdminRepository;
import cn.laterya.ai.domain.admin.model.entity.GatewayConfigEntity;
import cn.laterya.ai.domain.admin.service.IAdminService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService implements IAdminService {

    @Resource
    private IAdminRepository adminRepository;

    @Override
    public List<GatewayConfigEntity> queryGatewayConfigList() {
        return adminRepository.queryGatewayConfigList();
    }

}
