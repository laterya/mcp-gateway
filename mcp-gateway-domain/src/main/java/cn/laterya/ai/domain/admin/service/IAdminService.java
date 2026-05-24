package cn.laterya.ai.domain.admin.service;

import cn.laterya.ai.domain.admin.model.entity.GatewayConfigEntity;

import java.util.List;

/** 管理端领域服务接口 */
public interface IAdminService {

    List<GatewayConfigEntity> queryGatewayConfigList();

}
