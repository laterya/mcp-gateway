package cn.laterya.ai.domain.protocol.service.storage;

import cn.laterya.ai.domain.protocol.adapter.repository.IProtocolRepository;
import cn.laterya.ai.domain.protocol.model.entity.StorageCommandEntity;
import cn.laterya.ai.domain.protocol.service.IProtocolStorage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 协议存储服务
 *
 * <p>薄层编排：接收存储命令 → 提取 HTTPProtocolVO 列表 → 委托仓储端口持久化。
 * 业务逻辑在领域层，实际的 CRUD 细节在基础设施层。
 */
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

}
