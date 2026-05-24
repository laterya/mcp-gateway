package cn.laterya.ai.cases.admin;

import cn.laterya.ai.domain.admin.model.entity.GatewayConfigEntity;

import java.util.List;

/** 运营管理编排 */
public interface IAdminManageService {

    List<GatewayConfigEntity> queryGatewayConfigList();

}
