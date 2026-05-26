package cn.laterya.ai.infrastructure.dao;

import cn.laterya.ai.infrastructure.dao.po.McpGatewayAuthPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IMcpGatewayAuthDao {

    void insert(McpGatewayAuthPO mcpGatewayAuthPO);

    void deleteById(Long id);

    void updateById(McpGatewayAuthPO mcpGatewayAuthPO);

    McpGatewayAuthPO queryById(Long id);

    McpGatewayAuthPO queryByGatewayId(String gatewayId);

    McpGatewayAuthPO queryByGatewayIdAndApiKey(McpGatewayAuthPO req);

    int queryEffectiveGatewayAuthCount(String gatewayId);

    List<McpGatewayAuthPO> queryAll();

    void deleteByGatewayId(String gatewayId);

}
