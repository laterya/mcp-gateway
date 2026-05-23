package cn.laterya.ai.infrastructure.dao;

import cn.laterya.ai.infrastructure.dao.po.McpGatewayPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IMcpGatewayDao {

    void insert(McpGatewayPO mcpGatewayPO);

    void deleteById(Long id);

    void updateById(McpGatewayPO mcpGatewayPO);

    McpGatewayPO queryById(Long id);

    McpGatewayPO queryByGatewayId(String gatewayId);

    List<McpGatewayPO> queryAll();

}
