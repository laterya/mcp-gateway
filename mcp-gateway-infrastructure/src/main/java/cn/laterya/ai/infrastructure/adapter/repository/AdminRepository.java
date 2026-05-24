package cn.laterya.ai.infrastructure.adapter.repository;

import cn.laterya.ai.domain.admin.adapter.repository.IAdminRepository;
import cn.laterya.ai.domain.admin.model.entity.GatewayConfigEntity;
import cn.laterya.ai.infrastructure.dao.IMcpGatewayDao;
import cn.laterya.ai.infrastructure.dao.po.McpGatewayPO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理端仓储实现 —— PO 列表 → Entity 列表转换
 */
@Slf4j
@Repository
public class AdminRepository implements IAdminRepository {

    @Resource
    private IMcpGatewayDao mcpGatewayDao;

    @Override
    public List<GatewayConfigEntity> queryGatewayConfigList() {
        List<McpGatewayPO> pos = mcpGatewayDao.queryAll();
        return pos.stream().map(po -> GatewayConfigEntity.builder()
                .gatewayId(po.getGatewayId())
                .gatewayName(po.getGatewayName())
                .gatewayDesc(po.getGatewayDesc())
                .version(po.getVersion())
                .auth(po.getAuth())
                .status(po.getStatus())
                .build()).collect(Collectors.toList());
    }

}
