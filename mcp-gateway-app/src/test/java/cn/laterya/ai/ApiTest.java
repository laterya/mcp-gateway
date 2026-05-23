package cn.laterya.ai;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.model.openai.autoconfigure.OpenAiAudioSpeechAutoConfiguration;
import org.springframework.ai.model.openai.autoconfigure.OpenAiAudioTranscriptionAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.annotation.Resource;
import java.time.Duration;

/**
 * MCP 网关服务测试
 *
 * <p>使用方式：先启动网关服务（mvn spring-boot:run -pl mcp-gateway-app），再执行测试。
 *
 * <p>测试流程：
 * 1. McpSyncClient 通过 SSE 连接网关 → 触发 initialize 握手
 * 2. SyncMcpToolCallbackProvider 从网关拉取 tools/list
 * 3. ChatClient 发送 prompt → AI 判断需要调用 toUpperCase → 网关 ToolsCallHandler 处理 → 返回结果
 */
@Slf4j
@ActiveProfiles("test")
@SpringBootTest
@EnableAutoConfiguration(exclude = {
        OpenAiAudioSpeechAutoConfiguration.class,
        OpenAiAudioTranscriptionAutoConfiguration.class
})
public class ApiTest {

    @Resource
    private ChatClient.Builder chatClientBuilder;

    @Test
    public void test_mcp_toUpperCase() {
        ChatClient chatClient = chatClientBuilder.defaultOptions(
                        OpenAiChatOptions.builder()
                                .model("gpt-5.2")
                                .toolCallbacks(new org.springframework.ai.mcp.SyncMcpToolCallbackProvider(
                                        sseMcpClient()).getToolCallbacks())
                                .build())
                .build();

        log.info("测试结果:{}", chatClient.prompt("把pan转换为大写").call().content());
    }

    @Test
    public void test_mcp_listTools() {
        ChatClient chatClient = chatClientBuilder.defaultOptions(
                        OpenAiChatOptions.builder()
                                .model("gpt-5.2")
                                .toolCallbacks(new org.springframework.ai.mcp.SyncMcpToolCallbackProvider(
                                        sseMcpClient()).getToolCallbacks())
                                .build())
                .build();

        log.info("测试结果:{}", chatClient.prompt("有哪些工具可以使用").call().content());
    }

    /**
     * 连接本地网关的 MCP Client
     *
     * <p>McpSyncClient 内部会自动完成：
     * 1. GET /api-gateway/test10001/mcp/sse → 建立 SSE 流
     * 2. POST initialize → 握手
     * 3. POST tools/list → 拉取工具列表
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
