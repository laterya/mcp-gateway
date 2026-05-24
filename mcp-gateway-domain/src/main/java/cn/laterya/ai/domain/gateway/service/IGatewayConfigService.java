package cn.laterya.ai.domain.gateway.service;

import cn.laterya.ai.domain.gateway.model.entity.GatewayConfigCommandEntity;

/** 网关配置服务接口 */
public interface IGatewayConfigService {

    void saveGatewayConfig(GatewayConfigCommandEntity commandEntity);

    void updateGatewayAuthStatus(GatewayConfigCommandEntity commandEntity);

}
