package cn.laterya.ai.domain.session.service.message.handler;

import cn.laterya.ai.domain.session.model.McpSchemaVO;
import cn.laterya.ai.domain.session.service.message.IRequestHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 工具列表处理器
 *
 * <p>返回网关当前暴露的工具列表（硬编码案例），每个工具包含：
 * <ul>
 *   <li>name：工具名称，AI 根据此名称调用</li>
 *   <li>description：工具描述，AI 用来判断何时调用</li>
 *   <li>inputSchema：参数 JSON Schema，告诉 AI 入参结构</li>
 * </ul>
 *
 * <p>本案例提供一个 toUpperCase 工具，用于小写转大写。
 * 后续会从数据库配置读取，替换硬编码。
 */
@Slf4j
@Service("toolsListHandler")
public class ToolsListHandler implements IRequestHandler {

    @Override
    public McpSchemaVO.JSONRPCResponse handle(McpSchemaVO.JSONRPCRequest message) {
        return new McpSchemaVO.JSONRPCResponse("2.0", message.id(), Map.of(
                "tools", new Object[]{
                        Map.of(
                                "name", "toUpperCase",
                                "description", "小写转大写",
                                "inputSchema", Map.of(
                                        "type", "object",
                                        "properties", Map.of(
                                                "word", Map.of(
                                                        "type", "string",
                                                        "description", "单词，字符串"
                                                )
                                        ),
                                        "required", new String[]{"word"}
                                )
                        )
                }
        ), null);
    }

}
