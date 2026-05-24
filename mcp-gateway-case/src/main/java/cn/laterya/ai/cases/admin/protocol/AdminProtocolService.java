package cn.laterya.ai.cases.admin.protocol;

import cn.laterya.ai.cases.admin.IAdminProtocolService;
import cn.laterya.ai.domain.protocol.model.entity.StorageCommandEntity;
import cn.laterya.ai.domain.protocol.service.IProtocolStorage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdminProtocolService implements IAdminProtocolService {

    @Resource
    private IProtocolStorage protocolStorage;

    @Override
    public void saveGatewayProtocol(StorageCommandEntity commandEntity) {
        protocolStorage.doStorage(commandEntity);
    }

}
