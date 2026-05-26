package cn.laterya.ai.cases.mcp.sse.chain.node;

import cn.laterya.ai.cases.mcp.sse.chain.AbstractSessionChainNode;
import cn.laterya.ai.cases.mcp.sse.chain.SessionChainContext;
import cn.laterya.ai.domain.auth.model.entity.LicenseCommandEntity;
import cn.laterya.ai.domain.auth.service.IAuthLicenseService;
import cn.laterya.ai.types.enums.McpErrorCodes;
import cn.laterya.ai.types.exception.AppException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * SSE 传输 — 鉴权节点
 */
@Slf4j
@Component("sseVerifyNode")
public class VerifyNode extends AbstractSessionChainNode {

    @Resource
    private IAuthLicenseService authLicenseService;

    @Override
    protected Flux<ServerSentEvent<String>> doHandle(String gatewayId, SessionChainContext context) {
        log.info("鉴权校验 gatewayId:{}", gatewayId);

        boolean isValid = authLicenseService.checkLicense(new LicenseCommandEntity(gatewayId, context.getApiKey()));
        if (!isValid) {
            log.warn("鉴权失败 gatewayId:{} apiKey:{}", gatewayId, context.getApiKey());
            throw new AppException(McpErrorCodes.INSUFFICIENT_PERMISSIONS, "fail to auth apikey");
        }

        return fireNext(gatewayId, context);
    }

}
