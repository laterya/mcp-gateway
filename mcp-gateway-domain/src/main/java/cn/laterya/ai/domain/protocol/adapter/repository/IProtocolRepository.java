package cn.laterya.ai.domain.protocol.adapter.repository;

import cn.laterya.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;

import java.util.List;

/**
 * 协议仓储端口（DDD Port）
 *
 * <p>domain 层定义接口，infrastructure 层实现。
 * 职责：将 HTTPProtocolVO 转换为 PO 并写入 mcp_protocol_http + mcp_protocol_mapping 两张表。
 */
public interface IProtocolRepository {

    /**
     * 保存 HTTP 协议配置及字段映射
     *
     * @param httpProtocolVOS 解析后待存储的协议列表
     * @return 生成的协议 ID 列表（8位数字唯一标识）
     */
    List<Long> saveHttpProtocolAndMapping(List<HTTPProtocolVO> httpProtocolVOS);

}
