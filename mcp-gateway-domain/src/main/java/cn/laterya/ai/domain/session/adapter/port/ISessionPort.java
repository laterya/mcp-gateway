package cn.laterya.ai.domain.session.adapter.port;

import cn.laterya.ai.domain.session.model.valobj.McpToolProtocolConfigVO;

import java.io.IOException;

/**
 * 会话端口接口（领域适配器）
 *
 * <p>领域层通过此接口发起外部协议调用（HTTP/RPC/MQ 等）。
 * infrastructure 模块负责实现具体的协议调用逻辑。
 */
public interface ISessionPort {

    /**
     * 工具调用：根据协议配置和参数，调用后端接口
     */
    Object toolCall(McpToolProtocolConfigVO.HTTPConfig httpConfig, Object params) throws IOException;

}
