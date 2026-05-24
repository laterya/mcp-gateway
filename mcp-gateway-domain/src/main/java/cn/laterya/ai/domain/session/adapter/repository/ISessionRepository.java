package cn.laterya.ai.domain.session.adapter.repository;

import cn.laterya.ai.domain.session.model.valobj.McpGatewayConfigVO;
import cn.laterya.ai.domain.session.model.valobj.McpToolConfigVO;
import cn.laterya.ai.domain.session.model.valobj.McpToolProtocolConfigVO;

import java.util.List;

/**
 * 会话仓储接口（领域适配器 / ACL 防腐层）
 *
 * <p>领域层通过此接口获取基础设施层的数据，不直接依赖 DAO。
 * infrastructure 模块负责实现。
 */
public interface ISessionRepository {

    McpGatewayConfigVO queryMcpGatewayConfigByGatewayId(String gatewayId);

    List<McpToolConfigVO> queryMcpGatewayToolConfigListByGatewayId(String gatewayId);

    McpToolProtocolConfigVO queryMcpGatewayProtocolConfig(String gatewayId, String toolName);

}
