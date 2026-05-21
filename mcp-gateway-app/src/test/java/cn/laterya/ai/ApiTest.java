package cn.laterya.ai;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.annotation.Resource;
import java.time.Duration;

/**
 * MCP 网关服务测试
 *
 * <p>使用方式：先 debug 方式启动网关服务，再执行测试方法。
 * 本节 SSE 推送尚未接通，测试仅用于 debug 观察消息收发流程。
 */
@Slf4j
@SpringBootTest
public class ApiTest {

    @Resource
    private ChatClient.Builder chatClientBuilder;

    @Test
    public void test_mcp() {
        ChatClient chatClient = chatClientBuilder.defaultOptions(
                        OpenAiChatOptions.builder()
                                .model("gpt-4.1-mini")
                                .toolCallbacks(new org.springframework.ai.mcp.SyncMcpToolCallbackProvider(sseMcpClient()).getToolCallbacks())
                                .build())
                .build();

        log.info("测试结果:{}", chatClient.prompt("有哪些工具可以使用").call().content());
    }

    /**
     * 连接本地网关的 MCP Client
     */
    public McpSyncClient sseMcpClient() {
        HttpClientSseClientTransport transport = HttpClientSseClientTransport
                .builder("http://127.0.0.1:8090")
                .sseEndpoint("/api-gateway/test10001/mcp/sse")
                .build();

        McpSyncClient mcpSyncClient = McpClient.sync(transport)
                .requestTimeout(Duration.ofMinutes(60))
                .build();
        var initResult = mcpSyncClient.initialize();
        log.info("MCP 握手结果: {}", initResult);

        return mcpSyncClient;
    }

}
