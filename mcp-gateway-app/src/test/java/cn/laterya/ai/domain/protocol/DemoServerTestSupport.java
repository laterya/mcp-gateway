package cn.laterya.ai.domain.protocol;

import lombok.extern.slf4j.Slf4j;

import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * demo-server 可达性检测 + OpenAPI JSON 拉取，供 Protocol 测试类共享
 */
@Slf4j
public final class DemoServerTestSupport {

    public static final String DEMO_SERVER_API_DOCS = "http://localhost:8701/v3/api-docs";
    private static final int DEMO_SERVER_PORT = 8701;

    private static Boolean reachable;

    private DemoServerTestSupport() {}

    /** 缓存结果，整个 JVM 只检测一次 */
    public static boolean isDemoServerRunning() {
        if (reachable == null) {
            try (Socket socket = new Socket("localhost", DEMO_SERVER_PORT)) {
                reachable = true;
            } catch (Exception e) {
                log.warn("demo-server 未启动 (localhost:{})，跳过相关测试", DEMO_SERVER_PORT);
                reachable = false;
            }
        }
        return reachable;
    }

    public static String fetchOpenApiJson() throws Exception {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(DEMO_SERVER_API_DOCS))
                    .GET().build();
            return client.send(req, HttpResponse.BodyHandlers.ofString()).body();
        }
    }
}
