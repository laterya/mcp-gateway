package cn.laterya.ai.infrastructure.adapter.repository;

import cn.laterya.ai.domain.gateway.adapter.repository.IGatewayRepository;
import cn.laterya.ai.domain.gateway.model.entity.GatewayConfigCommandEntity;
import cn.laterya.ai.domain.gateway.model.entity.GatewayToolConfigCommandEntity;
import cn.laterya.ai.domain.gateway.model.valobj.GatewayConfigVO;
import cn.laterya.ai.domain.gateway.model.valobj.GatewayToolConfigVO;
import cn.laterya.ai.infrastructure.dao.IMcpGatewayDao;
import cn.laterya.ai.infrastructure.dao.IMcpGatewayToolDao;
import cn.laterya.ai.infrastructure.dao.po.McpGatewayPO;
import cn.laterya.ai.infrastructure.dao.po.McpGatewayToolPO;
import cn.laterya.ai.types.enums.GatewayEnum;
import cn.laterya.ai.types.enums.ResponseCode;
import cn.laterya.ai.types.exception.AppException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * 网关仓储实现 —— infrastructure 层适配器
 *
 * <p>实现 domain gateway 的 IGatewayRepository 端口。
 * save 操作 VO → PO 转换后调用 DAO insert，
 * update 操作限制影响行数 == 1，否则抛 DB_UPDATE_FAIL。
 */
@Slf4j
@Repository
public class GatewayRepository implements IGatewayRepository {

    @Resource
    private IMcpGatewayDao mcpGatewayDao;

    @Resource
    private IMcpGatewayToolDao mcpGatewayToolDao;

    @Override
    public void saveGatewayConfig(GatewayConfigCommandEntity commandEntity) {
        GatewayConfigVO vo = commandEntity.getGatewayConfigVO();

        McpGatewayPO po = McpGatewayPO.builder()
                .gatewayId(vo.getGatewayId())
                .gatewayName(vo.getGatewayName())
                .gatewayDesc(vo.getGatewayDesc())
                .version(vo.getVersion())
                .auth(vo.getAuth() != null ? vo.getAuth().getCode() : GatewayEnum.GatewayAuthStatusEnum.ENABLE.getCode())
                .status(vo.getStatus() != null ? vo.getStatus().getCode() : GatewayEnum.GatewayStatus.NOT_VERIFIED.getCode())
                .build();
        mcpGatewayDao.insert(po);
    }

    @Override
    public void updateGatewayAuthStatus(GatewayConfigCommandEntity commandEntity) {
        GatewayConfigVO vo = commandEntity.getGatewayConfigVO();
        if (vo.getAuth() == null) return;

        McpGatewayPO po = McpGatewayPO.builder()
                .gatewayId(vo.getGatewayId())
                .auth(vo.getAuth().getCode())
                .build();
        int count = mcpGatewayDao.updateAuthStatusByGatewayId(po);
        if (0 == count) {
            throw new AppException(ResponseCode.DB_UPDATE_FAIL.getCode(), ResponseCode.DB_UPDATE_FAIL.getInfo());
        }
    }

    @Override
    public void saveGatewayToolConfig(GatewayToolConfigCommandEntity commandEntity) {
        GatewayToolConfigVO vo = commandEntity.getGatewayToolConfigVO();

        McpGatewayToolPO po = McpGatewayToolPO.builder()
                .gatewayId(vo.getGatewayId())
                .toolId(vo.getToolId())
                .toolName(vo.getToolName())
                .toolType(vo.getToolType())
                .toolDescription(vo.getToolDescription())
                .toolVersion(vo.getToolVersion())
                .protocolId(vo.getProtocolId())
                .protocolType(vo.getProtocolType())
                .build();
        mcpGatewayToolDao.insert(po);
    }

    @Override
    public void updateGatewayToolProtocol(GatewayToolConfigCommandEntity commandEntity) {
        GatewayToolConfigVO vo = commandEntity.getGatewayToolConfigVO();

        McpGatewayToolPO po = McpGatewayToolPO.builder()
                .gatewayId(vo.getGatewayId())
                .protocolId(vo.getProtocolId())
                .protocolType(vo.getProtocolType())
                .build();
        int count = mcpGatewayToolDao.updateProtocolByGatewayId(po);
        if (0 == count) {
            throw new AppException(ResponseCode.DB_UPDATE_FAIL.getCode(), ResponseCode.DB_UPDATE_FAIL.getInfo());
        }
    }

}
