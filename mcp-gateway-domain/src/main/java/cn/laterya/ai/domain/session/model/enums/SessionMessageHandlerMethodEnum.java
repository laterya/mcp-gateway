package cn.laterya.ai.domain.session.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * MCP 方法名 → 处理器 Bean 名的映射枚举
 *
 * <p>策略模式的"路由表"：JSON-RPC 请求中的 method 字符串
 * 通过此枚举映射到对应的 Spring Bean 名称。
 */
@Getter
@AllArgsConstructor
public enum SessionMessageHandlerMethodEnum {

    INITIALIZE("initialize", "initializeHandler", "协议握手"),
    TOOLS_LIST("tools/list", "toolsListHandler", "工具列表请求"),
    TOOLS_CALL("tools/call", "toolsCallHandler", "工具调用请求"),
    RESOURCES_LIST("resources/list", "resourcesListHandler", "资源列表请求"),
    ;

    private final String method;
    private final String handlerName;
    private final String description;

    /**
     * 根据 JSON-RPC method 字符串查找枚举
     *
     * @param method 方法名
     * @return 对应枚举，找不到返回 null
     */
    public static SessionMessageHandlerMethodEnum getByMethod(String method) {
        for (SessionMessageHandlerMethodEnum value : values()) {
            if (value.getMethod().equals(method)) {
                return value;
            }
        }
        return null;
    }

}
