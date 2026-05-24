package cn.laterya.ai.cases.admin;

import cn.laterya.ai.domain.protocol.model.entity.StorageCommandEntity;

public interface IAdminProtocolService {
    void saveGatewayProtocol(StorageCommandEntity commandEntity);
    void deleteGatewayProtocol(Long protocolId);
}
