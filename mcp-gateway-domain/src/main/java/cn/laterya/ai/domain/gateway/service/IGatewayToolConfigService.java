package cn.laterya.ai.domain.gateway.service;

import cn.laterya.ai.domain.gateway.model.entity.GatewayToolConfigCommandEntity;

/** 网关工具配置服务接口 */
public interface IGatewayToolConfigService {

    void saveGatewayToolConfig(GatewayToolConfigCommandEntity commandEntity);

    void updateGatewayToolProtocol(GatewayToolConfigCommandEntity commandEntity);

}
