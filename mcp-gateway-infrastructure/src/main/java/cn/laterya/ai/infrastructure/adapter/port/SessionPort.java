package cn.laterya.ai.infrastructure.adapter.port;

import cn.laterya.ai.domain.session.adapter.port.ISessionPort;
import cn.laterya.ai.domain.session.model.valobj.McpToolProtocolConfigVO;
import cn.laterya.ai.infrastructure.gateway.GenericHttpGateway;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.springframework.stereotype.Component;
import retrofit2.Call;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 会话端口实现：HTTP 协议调用
 */
@Slf4j
@Component
public class SessionPort implements ISessionPort {

    @Resource
    private GenericHttpGateway gateway;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Object toolCall(McpToolProtocolConfigVO.HTTPConfig httpConfig, Object params) throws IOException {
        // 1. 解析请求头
        Map<String, Object> headers = new HashMap<>();
        if (httpConfig.getHttpHeaders() != null) {
            headers = objectMapper.readValue(httpConfig.getHttpHeaders(), Map.class);
        }

        // 2. 参数校验
        if (!(params instanceof Map<?, ?> arguments)) {
            throw new IllegalArgumentException("工具调用参数格式不正确");
        }

        String httpMethod = httpConfig.getHttpMethod().toLowerCase();

        switch (httpMethod) {
            case "post": {
                Object body = ((Map<String, Object>) arguments).values().toArray()[0];
                String jsonBody = objectMapper.writeValueAsString(body);
                RequestBody requestBody = RequestBody.create(jsonBody, MediaType.parse("application/json"));

                Call<ResponseBody> call = gateway.post(httpConfig.getHttpUrl(), headers, requestBody);
                ResponseBody responseBody = call.execute().body();
                return responseBody != null ? responseBody.string() : null;
            }
            case "get": {
                Map<String, Object> queryParams = new HashMap<>((Map<String, Object>) arguments.values().toArray()[0]);
                String url = httpConfig.getHttpUrl();

                Matcher matcher = Pattern.compile("\\{([^}]+)\\}").matcher(url);
                while (matcher.find()) {
                    String name = matcher.group(1);
                    if (queryParams.containsKey(name)) {
                        url = url.replace("{" + name + "}", String.valueOf(queryParams.get(name)));
                        queryParams.remove(name);
                    }
                }

                Call<ResponseBody> call = gateway.get(url, headers, queryParams);
                ResponseBody responseBody = call.execute().body();
                return responseBody != null ? responseBody.string() : null;
            }
            default:
                throw new IllegalArgumentException("不支持的 HTTP 方法: " + httpMethod);
        }
    }

}
