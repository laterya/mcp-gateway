package cn.laterya.ai.cases.mcp.chain.node;

import cn.laterya.ai.cases.mcp.chain.AbstractSessionChainNode;
import cn.laterya.ai.cases.mcp.chain.SessionChainContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * 根节点 —— 链路入口，负责日志记录
 *
 * <p>类比：工厂流水线的第一站，登记来访信息后放行。
 * 将来可在此添加限流、黑白名单等前置逻辑。
 */
@Slf4j
@Component
public class RootNode extends AbstractSessionChainNode {

    @Override
    protected Flux<ServerSentEvent<String>> doHandle(String gatewayId, SessionChainContext context) {
        log.info("MCP SSE 会话编排开始 gatewayId:{}", gatewayId);
        return fireNext(gatewayId, context);
    }

}
