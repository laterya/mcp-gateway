package cn.laterya.ai.cases.mcp;

import cn.laterya.ai.cases.mcp.chain.AbstractSessionChainNode;
import cn.laterya.ai.cases.mcp.chain.SessionChainContext;
import cn.laterya.ai.cases.mcp.chain.node.CreateSessionNode;
import cn.laterya.ai.cases.mcp.chain.node.RootNode;
import cn.laterya.ai.cases.mcp.chain.node.SseResponseNode;
import cn.laterya.ai.cases.mcp.chain.node.VerifyNode;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * MCP 会话服务 —— 编排层（case 层）
 *
 * <p>职责：组装责任链并触发执行。
 * 本类不包含业务逻辑，只做"串联"——把 domain 层的各服务通过节点编排成完整流程。
 *
 * <p>链路：RootNode → VerifyNode → CreateSessionNode → SseResponseNode
 * <pre>
 * RootNode          记录日志，放行
 *   ↓
 * VerifyNode        验证参数（占位，后续补充）
 *   ↓
 * CreateSessionNode 调用 domain 层创建会话，结果写入上下文
 *   ↓
 * SseResponseNode   构建并返回 SSE 流（终端节点）
 * </pre>
 */
@Slf4j
@Service
public class McpSessionService implements IMcpSessionService {

    @Resource
    private RootNode rootNode;
    @Resource
    private VerifyNode verifyNode;
    @Resource
    private CreateSessionNode createSessionNode;
    @Resource
    private SseResponseNode sseResponseNode;

    private AbstractSessionChainNode chain;

    /**
     * 应用启动后组装责任链
     *
     * <p>为什么不直接 new 节点？
     * 所有节点都是 Spring Bean（@Component），由容器管理依赖注入。
     * 在 @PostConstruct 中拿到所有 Bean 后再组装链路，确保依赖已就绪。
     */
    @PostConstruct
    public void initChain() {
        chain = rootNode;
        rootNode.linkWith(verifyNode)
                .linkWith(createSessionNode)
                .linkWith(sseResponseNode);

        log.info("MCP 会话编排链初始化完成: RootNode → VerifyNode → CreateSessionNode → SseResponseNode");
    }

    @Override
    public Flux<ServerSentEvent<String>> createMcpSession(String gatewayId) {
        return chain.handle(gatewayId, new SessionChainContext());
    }

}
