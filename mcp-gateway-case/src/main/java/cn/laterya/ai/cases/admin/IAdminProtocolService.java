package cn.laterya.ai.cases.admin;

import cn.laterya.ai.domain.protocol.model.entity.StorageCommandEntity;

/** 协议配置管理编排 */
public interface IAdminProtocolService {

    void saveGatewayProtocol(StorageCommandEntity commandEntity);

}
