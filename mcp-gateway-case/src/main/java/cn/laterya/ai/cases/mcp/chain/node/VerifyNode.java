package cn.laterya.ai.cases.mcp.chain.node;

import cn.laterya.ai.cases.mcp.chain.AbstractSessionChainNode;
import cn.laterya.ai.cases.mcp.chain.SessionChainContext;
import cn.laterya.ai.domain.auth.model.entity.LicenseCommandEntity;
import cn.laterya.ai.domain.auth.service.IAuthLicenseService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * 鉴权节点 —— 校验 api_key 是否有效
 */
@Slf4j
@Component
public class VerifyNode extends AbstractSessionChainNode {

    @Resource
    private IAuthLicenseService authLicenseService;

    @Override
    protected Flux<ServerSentEvent<String>> doHandle(String gatewayId, SessionChainContext context) {
        log.info("鉴权校验 gatewayId:{}", gatewayId);

        authLicenseService.checkLicenseOrThrow(new LicenseCommandEntity(gatewayId, context.getApiKey()));

        return fireNext(gatewayId, context);
    }

}
