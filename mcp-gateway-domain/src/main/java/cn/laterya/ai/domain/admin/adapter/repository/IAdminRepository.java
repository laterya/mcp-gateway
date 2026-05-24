package cn.laterya.ai.domain.admin.adapter.repository;

import cn.laterya.ai.domain.admin.model.entity.GatewayConfigEntity;

import java.util.List;

/** 管理端仓储端口 */
public interface IAdminRepository {

    List<GatewayConfigEntity> queryGatewayConfigList();

}
