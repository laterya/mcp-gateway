package cn.laterya.ai.cases.mcp.chain.session;

import cn.laterya.ai.cases.mcp.chain.AbstractChainNode;
import cn.laterya.ai.cases.mcp.chain.SessionChainContext;
import cn.laterya.ai.domain.auth.model.entity.LicenseCommandEntity;
import cn.laterya.ai.domain.auth.service.IAuthLicenseService;
import cn.laterya.ai.types.enums.McpErrorCodes;
import cn.laterya.ai.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;

/**
 * 共享会话链鉴权节点
 */
@Slf4j
public class SessionVerifyNode<R> extends AbstractChainNode<SessionChainContext, R> {

    private final IAuthLicenseService authLicenseService;

    public SessionVerifyNode(IAuthLicenseService authLicenseService) {
        this.authLicenseService = authLicenseService;
    }

    @Override
    protected R doHandle(SessionChainContext context) {
        log.info("鉴权校验 gatewayId:{}", context.getGatewayId());

        boolean isValid = authLicenseService.checkLicense(
                new LicenseCommandEntity(context.getGatewayId(), context.getApiKey()));
        if (!isValid) {
            log.warn("鉴权失败 gatewayId:{} apiKey:{}", context.getGatewayId(), context.getApiKey());
            throw new AppException(McpErrorCodes.INSUFFICIENT_PERMISSIONS, "fail to auth apikey");
        }

        return fireNext(context);
    }

}
