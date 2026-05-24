package cn.laterya.ai.infrastructure.dao;

import cn.laterya.ai.infrastructure.dao.po.McpGatewayToolPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IMcpGatewayToolDao {

    void insert(McpGatewayToolPO mcpGatewayToolPO);

    void deleteById(Long id);

    void updateById(McpGatewayToolPO mcpGatewayToolPO);

    McpGatewayToolPO queryById(Long id);

    McpGatewayToolPO queryByGatewayIdAndToolName(String gatewayId, String toolName);

    List<McpGatewayToolPO> queryByGatewayId(String gatewayId);

    List<McpGatewayToolPO> queryAll();

    int updateProtocolByGatewayId(McpGatewayToolPO mcpGatewayToolPO);

}
