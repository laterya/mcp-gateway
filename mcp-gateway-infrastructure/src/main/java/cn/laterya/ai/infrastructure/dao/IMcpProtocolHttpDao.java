package cn.laterya.ai.infrastructure.dao;

import cn.laterya.ai.infrastructure.dao.po.McpProtocolHttpPO;
import org.apache.ibatis.annotations.Mapper;

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

}
