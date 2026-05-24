package cn.laterya.ai.domain.protocol.service;

import cn.laterya.ai.domain.protocol.model.entity.StorageCommandEntity;
import java.util.List;

public interface IProtocolStorage {
    List<Long> doStorage(StorageCommandEntity commandEntity);
    void deleteByProtocolId(Long protocolId);
}
