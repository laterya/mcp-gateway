package cn.laterya.ai.cases.admin;

import cn.laterya.ai.domain.admin.model.entity.*;
import cn.laterya.ai.domain.auth.model.entity.RegisterCommandEntity;
import cn.laterya.ai.domain.gateway.model.entity.GatewayConfigCommandEntity;
import cn.laterya.ai.domain.gateway.model.entity.GatewayToolConfigCommandEntity;
import cn.laterya.ai.domain.protocol.model.entity.StorageCommandEntity;

import java.util.List;

/**
 * Admin 限界上下文编排服务 — 合并原 4 个纯透传 case service
 * （Manage / Gateway / Protocol / Auth）
 */
public interface IAdminOrchestrationService {

    // ===== Gateway 配置 =====
    void saveGatewayConfig(GatewayConfigCommandEntity commandEntity);
    void saveGatewayToolConfig(GatewayToolConfigCommandEntity commandEntity);

    // ===== Protocol =====
    void saveGatewayProtocol(StorageCommandEntity commandEntity);
    void deleteGatewayProtocol(Long protocolId);

    // ===== Auth =====
    void saveGatewayAuth(RegisterCommandEntity commandEntity);
    void deleteGatewayAuth(String gatewayId);
    void updateGatewayAuth(String gatewayId, Integer rateLimit, String expireTime);

    // ===== 查询 =====
    List<GatewayConfigEntity> queryGatewayConfigList();
    GatewayConfigPageEntity queryGatewayConfigPage(GatewayConfigQueryEntity queryEntity);

    List<GatewayToolConfigEntity> queryGatewayToolList();
    GatewayToolPageEntity queryGatewayToolPage(GatewayToolQueryEntity queryEntity);
    List<GatewayToolConfigEntity> queryGatewayToolListByGatewayId(String gatewayId);

    List<GatewayProtocolConfigEntity> queryGatewayProtocolList();
    GatewayProtocolPageEntity queryGatewayProtocolPage(GatewayProtocolQueryEntity queryEntity);
    List<GatewayProtocolConfigEntity> queryGatewayProtocolListByGatewayId(String gatewayId);

    List<GatewayAuthConfigEntity> queryGatewayAuthList();
    GatewayAuthPageEntity queryGatewayAuthPage(GatewayAuthQueryEntity queryEntity);

}
