package cn.laterya.ai.cases.mcp.chain.node;

import cn.laterya.ai.cases.mcp.chain.AbstractSessionChainNode;
import cn.laterya.ai.cases.mcp.chain.SessionChainContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * 验证节点 —— 校验请求参数和权限
 *
 * <p>当前为占位实现（直接放行），后续课程会补充 api_key 验证等逻辑。
 * 放行后继续执行下一个节点。
 */
@Slf4j
@Component
public class VerifyNode extends AbstractSessionChainNode {

    @Override
    protected Flux<ServerSentEvent<String>> doHandle(String gatewayId, SessionChainContext context) {
        log.info("验证请求 gatewayId:{}", gatewayId);
        // todo: 后续补充 api_key 等验证逻辑
        return fireNext(gatewayId, context);
    }

}
