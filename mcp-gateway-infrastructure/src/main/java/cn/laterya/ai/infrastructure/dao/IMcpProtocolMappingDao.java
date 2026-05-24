package cn.laterya.ai.infrastructure.dao;

import cn.laterya.ai.infrastructure.dao.po.McpProtocolMappingPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IMcpProtocolMappingDao {

    void insert(McpProtocolMappingPO mcpProtocolMappingPO);

    void deleteById(Long id);

    void updateById(McpProtocolMappingPO mcpProtocolMappingPO);

    McpProtocolMappingPO queryById(Long id);

    List<McpProtocolMappingPO> queryByProtocolId(Long protocolId);

    List<McpProtocolMappingPO> queryAll();

    int deleteByProtocolId(Long protocolId);

}
