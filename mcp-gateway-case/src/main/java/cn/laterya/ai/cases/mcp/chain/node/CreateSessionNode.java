package cn.laterya.ai.cases.mcp.chain.node;

import cn.laterya.ai.cases.mcp.chain.AbstractSessionChainNode;
import cn.laterya.ai.cases.mcp.chain.SessionChainContext;
import cn.laterya.ai.domain.session.model.SessionConfigVO;
import cn.laterya.ai.domain.session.service.ISessionManagementService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * 创建会话节点 —— 调用 domain 层创建会话，结果写入上下文
 *
 * <p>这是编排的核心：将 domain 层的 ISessionManagementService.createSession()
 * 编排进链路。上下文像"流水线托盘"，把创建好的会话配置传递给下游节点。
 */
@Slf4j
@Component
public class CreateSessionNode extends AbstractSessionChainNode {

    @Resource
    private ISessionManagementService sessionManagementService;

    @Override
    protected Flux<ServerSentEvent<String>> doHandle(String gatewayId, SessionChainContext context) {
        log.info("创建会话 gatewayId:{}", gatewayId);

        SessionConfigVO sessionConfigVO = sessionManagementService.createSession(gatewayId, context.getApiKey());
        context.setSessionConfigVO(sessionConfigVO);

        return fireNext(gatewayId, context);
    }

}
