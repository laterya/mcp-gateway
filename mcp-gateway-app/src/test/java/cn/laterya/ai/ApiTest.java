package cn.laterya.ai;

import com.sun.net.httpserver.HttpServer;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.model.openai.autoconfigure.OpenAiAudioSpeechAutoConfiguration;
import org.springframework.ai.model.openai.autoconfigure.OpenAiAudioTranscriptionAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.annotation.Resource;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.Duration;

/**
 * MCP 网关服务测试
 *
 * <p>使用方式：先启动网关服务（mvn spring-boot:run -pl mcp-gateway-app），再执行测试。
 *
 * <p>测试流程：
 * 1. 启动 Mock 后端服务（8701 端口），模拟真实 HTTP 接口
 * 2. McpSyncClient 通过 SSE 连接网关 → 触发 initialize 握手
 * 3. SyncMcpToolCallbackProvider 从网关拉取 tools/list
 * 4. ChatClient 发送 prompt → AI 调用 getCompanyEmployee → 网关 ToolsCallHandler → Mock 后端 → 返回结果
 */
@Slf4j
@ActiveProfiles("test")
@SpringBootTest
@EnableAutoConfiguration(exclude = {
        OpenAiAudioSpeechAutoConfiguration.class,
        OpenAiAudioTranscriptionAutoConfiguration.class
})
public class ApiTest {

    private static HttpServer mockBackend;

    @Resource
    private ChatClient.Builder chatClientBuilder;

    @BeforeAll
    static void startMockBackend() throws Exception {
        mockBackend = HttpServer.create(new InetSocketAddress(8701), 0);
        mockBackend.createContext("/api/v1/mcp/get_company_employee", exchange -> {
            String response = """
                    {"salary":"16.92","dayManHour":"8","user":{"userId":"001","userName":"pan"}}""";
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        });
        mockBackend.start();
        log.info("Mock 后端服务启动 - http://localhost:8701");
    }

    @AfterAll
    static void stopMockBackend() {
        if (mockBackend != null) {
            mockBackend.stop(0);
            log.info("Mock 后端服务停止");
        }
    }

    @Test
    public void test_mcp_getCompanyEmployee() {
        ChatClient chatClient = chatClientBuilder.defaultOptions(
                        OpenAiChatOptions.builder()
                                .model("gpt-5.2")
                                .toolCallbacks(new org.springframework.ai.mcp.SyncMcpToolCallbackProvider(
                                        sseMcpClient()).getToolCallbacks())
                                .build())
                .build();

        log.info("测试结果:{}", chatClient.prompt("""
                查询公司雇员信息，信息如下：
                城市：beijing
                公司名称：jd
                公司类型：internet
                """).call().content());
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
     * 1. GET /api-gateway/gateway_001/mcp/sse → 建立 SSE 流
     * 2. POST initialize → 握手
     * 3. POST tools/list → 拉取工具列表
     */
    public McpSyncClient sseMcpClient() {
        HttpClientSseClientTransport transport = HttpClientSseClientTransport
                .builder("http://127.0.0.1:8090")
                .sseEndpoint("/api-gateway/gateway_001/mcp/sse")
                .build();

        McpSyncClient mcpSyncClient = McpClient.sync(transport)
                .requestTimeout(Duration.ofMinutes(60))
                .build();
        var initResult = mcpSyncClient.initialize();
        log.info("MCP 握手结果: {}", initResult);

        return mcpSyncClient;
    }

}
