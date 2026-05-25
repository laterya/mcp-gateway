package cn.laterya.ai.cases.mcp.streamable.node;

import cn.laterya.ai.cases.mcp.streamable.AbstractStreamableChainNode;
import cn.laterya.ai.cases.mcp.streamable.StreamableChainContext;
import cn.laterya.ai.domain.auth.model.entity.LicenseCommandEntity;
import cn.laterya.ai.domain.auth.service.IAuthLicenseService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Streamable HTTP 鉴权节点 —— 从 Authorization: Bearer 头提取 api_key 并校验
 */
@Slf4j
@Component
public class StreamableAuthNode extends AbstractStreamableChainNode {

    @Resource
    private IAuthLicenseService authLicenseService;

    @Override
    protected void doHandle(String gatewayId, StreamableChainContext context) {
        log.info("Streamable HTTP 鉴权校验 gatewayId:{}", gatewayId);

        authLicenseService.checkLicenseOrThrow(new LicenseCommandEntity(gatewayId, context.getApiKey()));

        fireNext(gatewayId, context);
    }

}
