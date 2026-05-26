package cn.laterya.ai.cases.mcp.streamable.session.node;

import cn.laterya.ai.cases.mcp.shared.session.SessionChainContext;
import cn.laterya.ai.cases.mcp.streamable.session.AbstractStreamableSessionChainNode;
import cn.laterya.ai.domain.auth.model.entity.LicenseCommandEntity;
import cn.laterya.ai.domain.auth.service.IAuthLicenseService;
import cn.laterya.ai.types.enums.McpErrorCodes;
import cn.laterya.ai.types.exception.AppException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Streamable HTTP — 鉴权节点（复用 SSE 的 IAuthLicenseService）
 */
@Slf4j
@Component("streamableVerifyNode")
public class StreamableVerifyNode extends AbstractStreamableSessionChainNode {

    @Resource
    private IAuthLicenseService authLicenseService;

    @Override
    protected void doHandle(String gatewayId, SessionChainContext context) {
        log.info("Streamable HTTP 鉴权校验 gatewayId:{}", gatewayId);

        boolean isValid = authLicenseService.checkLicense(new LicenseCommandEntity(gatewayId, context.getApiKey()));
        if (!isValid) {
            log.warn("Streamable HTTP 鉴权失败 gatewayId:{} apiKey:{}", gatewayId, context.getApiKey());
            throw new AppException(McpErrorCodes.INSUFFICIENT_PERMISSIONS, "fail to auth apikey");
        }

        fireNext(gatewayId, context);
    }

}
