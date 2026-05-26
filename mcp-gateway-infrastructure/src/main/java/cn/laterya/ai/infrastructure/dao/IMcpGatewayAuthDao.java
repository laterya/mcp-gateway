package cn.laterya.ai.infrastructure.dao;

import cn.laterya.ai.infrastructure.dao.po.McpGatewayAuthPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    List<McpGatewayAuthPO> queryPage(@Param("gatewayId") String gatewayId, @Param("apiKey") String apiKey,
                                     @Param("limit") int limit, @Param("offset") int offset);

    long queryCount(@Param("gatewayId") String gatewayId, @Param("apiKey") String apiKey);

}
