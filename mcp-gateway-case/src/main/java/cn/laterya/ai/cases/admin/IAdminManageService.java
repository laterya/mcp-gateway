package cn.laterya.ai.cases.admin;

import cn.laterya.ai.domain.admin.model.entity.*;

import java.util.List;

public interface IAdminManageService {

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
