package cn.laterya.ai.infrastructure.dao;

import cn.laterya.ai.infrastructure.dao.po.McpProtocolHttpPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IMcpProtocolHttpDao {

    void insert(McpProtocolHttpPO mcpProtocolHttpPO);

    void deleteById(Long id);

    void updateById(McpProtocolHttpPO mcpProtocolHttpPO);

    McpProtocolHttpPO queryById(Long id);

    McpProtocolHttpPO queryByProtocolId(Long protocolId);

    List<McpProtocolHttpPO> queryAll();

    int deleteByProtocolId(Long protocolId);

    List<McpProtocolHttpPO> queryPage(@Param("protocolId") Long protocolId, @Param("httpUrl") String httpUrl,
                                      @Param("limit") int limit, @Param("offset") int offset);

    long queryCount(@Param("protocolId") Long protocolId, @Param("httpUrl") String httpUrl);

}
