package cn.laterya.ai.cases.admin;

import cn.laterya.ai.domain.auth.model.entity.RegisterCommandEntity;

public interface IAdminAuthService {

    void saveGatewayAuth(RegisterCommandEntity commandEntity);

    void deleteGatewayAuth(String gatewayId);

}
