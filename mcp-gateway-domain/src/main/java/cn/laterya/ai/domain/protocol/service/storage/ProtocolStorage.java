package cn.laterya.ai.domain.protocol.service.storage;

import cn.laterya.ai.domain.protocol.adapter.repository.IProtocolRepository;
import cn.laterya.ai.domain.protocol.model.entity.StorageCommandEntity;
import cn.laterya.ai.domain.protocol.service.IProtocolStorage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProtocolStorage implements IProtocolStorage {

    @Resource
    private IProtocolRepository repository;

    @Override
    public List<Long> doStorage(StorageCommandEntity commandEntity) {
        log.info("协议存储数量:{}", commandEntity.getHttpProtocolVOS().size());
        return repository.saveHttpProtocolAndMapping(commandEntity.getHttpProtocolVOS());
    }

    @Override
    public void deleteByProtocolId(Long protocolId) {
        log.info("删除协议 protocolId:{}", protocolId);
        repository.deleteByProtocolId(protocolId);
    }
}
