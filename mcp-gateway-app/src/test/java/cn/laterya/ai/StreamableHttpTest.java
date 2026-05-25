package cn.laterya.ai;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Streamable HTTP 传输集成测试
 *
 * <p>自包含测试（@SpringBootTest 启动内嵌服务器），需 Docker MySQL 运行在 13306 端口。
 *
 * <p>测试覆盖：
 * 1. POST /{gatewayId}/mcp + Authorization: Bearer → initialize 成功，返回 InitializeResult + Mcp-Session-Id
 * 2. POST /{gatewayId}/mcp 无效 api_key → 鉴权失败
 */
@Slf4j
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StreamableHttpTest {

    private static final String GATEWAY_ID = "gateway_001";
    private static final String VALID_API_KEY = "RS590LKPOD8877DDLMFKS4";

    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * 成功场景：发送 initialize 请求，验证返回 InitializeResult + Mcp-Session-Id 响应头
     */
    @Test
    public void test_initialize_success() {
        String url = "/" + GATEWAY_ID + "/mcp";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + VALID_API_KEY);
        headers.set("Accept", "application/json, text/event-stream");

        String initializeBody = """
                {
                    "jsonrpc": "2.0",
                    "method": "initialize",
                    "id": 1,
                    "params": {
                        "protocolVersion": "2025-03-26",
                        "capabilities": {},
                        "clientInfo": {"name": "test-client", "version": "1.0.0"}
                    }
                }
                """;

        HttpEntity<String> request = new HttpEntity<>(initializeBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        log.info("Streamable HTTP initialize 响应 status:{} body:{}", response.getStatusCode(), response.getBody());

        // 验证 HTTP 状态码
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // 验证 Mcp-Session-Id 响应头存在
        String sessionId = response.getHeaders().getFirst("Mcp-Session-Id");
        assertNotNull(sessionId, "Mcp-Session-Id 响应头不能为空");
        assertFalse(sessionId.isEmpty(), "Mcp-Session-Id 不能为空字符串");

        // 验证响应 body 包含 InitializeResult
        String body = response.getBody();
        assertNotNull(body);
        assertTrue(body.contains("\"jsonrpc\":\"2.0\""), "响应应为 JSON-RPC 2.0 格式");
        assertTrue(body.contains("\"result\""), "响应应包含 result 字段");
        assertTrue(body.contains("\"protocolVersion\""), "响应应包含 protocolVersion");
        assertTrue(body.contains("\"capabilities\""), "响应应包含 capabilities");
        assertTrue(body.contains("\"serverInfo\""), "响应应包含 serverInfo");
    }

    /**
     * 鉴权失败场景：无效 api_key
     */
    @Test
    public void test_initialize_authFailure() {
        String url = "/" + GATEWAY_ID + "/mcp";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer INVALID_KEY");
        headers.set("Accept", "application/json, text/event-stream");

        String initializeBody = """
                {
                    "jsonrpc": "2.0",
                    "method": "initialize",
                    "id": 1,
                    "params": {
                        "protocolVersion": "2025-03-26",
                        "capabilities": {},
                        "clientInfo": {"name": "test-client", "version": "1.0.0"}
                    }
                }
                """;

        HttpEntity<String> request = new HttpEntity<>(initializeBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        log.info("Streamable HTTP 鉴权失败响应 status:{} body:{}", response.getStatusCode(), response.getBody());

        // 验证鉴权失败返回非 200
        assertNotEquals(HttpStatus.OK, response.getStatusCode(),
                "无效 api_key 不应返回 200");
    }

}
