package cn.laterya.ai.domain.session.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;

/**
 * MCP 协议消息结构定义
 *
 * <p>使用 JDK 16+ 新特性：
 * <ul>
 *   <li>sealed interface + permits：限制实现类，编译器级别类型安全</li>
 *   <li>record：不可变数据载体，自动生成 equals/hashCode/toString</li>
 * </ul>
 */
public final class McpSchemaVO {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private McpSchemaVO() {
    }

    /**
     * JSON-RPC 2.0 消息类型 sealed 接口
     *
     * <p>sealed + permits 限制只有 Request 和 Response 两种实现，
     * 在 switch 表达式和 instanceof 模式匹配时编译器能做穷举检查。
     */
    public sealed interface JSONRPCMessage permits JSONRPCRequest, JSONRPCResponse {

        String jsonrpc();

    }

    /**
     * JSON-RPC 2.0 请求对象
     *
     * @param jsonrpc 协议版本，固定 "2.0"
     * @param method  请求方法：initialize、tools/list、tools/call、resources/list
     * @param id      请求 ID（字符串或整数）
     * @param params  请求参数（任意 JSON 结构）
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record JSONRPCRequest(
            @JsonProperty("jsonrpc") String jsonrpc,
            @JsonProperty("method") String method,
            @JsonProperty("id") Object id,
            @JsonProperty("params") Object params
    ) implements JSONRPCMessage {
    }

    /**
     * JSON-RPC 2.0 响应对象
     *
     * @param jsonrpc 协议版本，固定 "2.0"
     * @param id      对应请求的 ID
     * @param result  成功时的响应结果
     * @param error   失败时的错误信息
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record JSONRPCResponse(
            @JsonProperty("jsonrpc") String jsonrpc,
            @JsonProperty("id") Object id,
            @JsonProperty("result") Object result,
            @JsonProperty("error") JSONRPCError error
    ) implements JSONRPCMessage {

        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record JSONRPCError(
                @JsonProperty("code") int code,
                @JsonProperty("message") String message,
                @JsonProperty("data") Object data
        ) {
        }

    }

    /**
     * 反序列化 JSON-RPC 消息
     *
     * <p>根据 JSON 内容自动判断是请求（含 method+id）还是响应（含 result 或 error）。
     */
    public static JSONRPCMessage deserializeJsonRpcMessage(String jsonText) {
        try {
            HashMap<String, Object> messageMap = OBJECT_MAPPER.readValue(jsonText, new TypeReference<>() {
            });

            if (messageMap.containsKey("method") && messageMap.containsKey("id")) {
                return OBJECT_MAPPER.convertValue(messageMap, JSONRPCRequest.class);
            }

            return OBJECT_MAPPER.convertValue(messageMap, JSONRPCResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("反序列化 JSON-RPC 消息失败", e);
        }
    }

}
