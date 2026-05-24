package cn.laterya.ai.cases.admin;

import cn.laterya.ai.domain.auth.model.entity.RegisterCommandEntity;

/** 认证配置管理编排 */
public interface IAdminAuthService {

    void saveGatewayAuth(RegisterCommandEntity commandEntity);

}
