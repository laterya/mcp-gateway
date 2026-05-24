package cn.laterya.ai.domain.llm.service.impl;

import cn.laterya.ai.domain.llm.service.ILLMService;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class LLMService implements ILLMService {

    @Resource
    private OpenAiChatModel baseChatModel;

    /**
     * ChatClient 缓存 — key 为 sseEndpoint（含 gatewayId + apiKey 的完整连接信息）
     *
     * <p>按 sseEndpoint 而非 gatewayId 缓存，因为同一网关不同 apiKey 对应不同的 MCP 客户端
     */
    private final Map<String, ChatClient> chatClientCache = new ConcurrentHashMap<>();

    @Override
    public String call(String baseUrl, String sseEndpoint, String message, long timeout, boolean reload) {
        if (reload) {
            chatClientCache.remove(sseEndpoint);
        }

        ChatClient chatClient = chatClientCache.computeIfAbsent(sseEndpoint, ep -> {
            log.info("构建 ChatClient sseEndpoint:{}", ep);
            return buildChatClient(baseUrl, ep, timeout);
        });

        log.info("调用 LLM message:{}", message);
        return chatClient.prompt(message).call().content();
    }

    /**
     * MCP SSE 传输 → 握手拉取工具列表 → 构建 ChatClient
     */
    private ChatClient buildChatClient(String baseUrl, String sseEndpoint, long timeout) {
        HttpClientSseClientTransport transport = HttpClientSseClientTransport
                .builder(baseUrl)
                .sseEndpoint(sseEndpoint)
                .build();

        McpSyncClient mcpSyncClient = McpClient.sync(transport)
                .requestTimeout(Duration.ofSeconds(timeout))
                .build();
        mcpSyncClient.initialize();

        return ChatClient.builder(baseChatModel)
                .defaultOptions(OpenAiChatOptions.builder()
                        .toolCallbacks(new SyncMcpToolCallbackProvider(mcpSyncClient).getToolCallbacks())
                        .build())
                .build();
    }
}
