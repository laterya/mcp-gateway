package cn.laterya.ai.infrastructure.adapter.repository;

import cn.laterya.ai.domain.protocol.adapter.repository.IProtocolRepository;
import cn.laterya.ai.domain.protocol.model.valobj.enums.ProtocolStatusEnum;
import cn.laterya.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;
import cn.laterya.ai.infrastructure.dao.IMcpProtocolHttpDao;
import cn.laterya.ai.infrastructure.dao.IMcpProtocolMappingDao;
import cn.laterya.ai.infrastructure.dao.po.McpProtocolHttpPO;
import cn.laterya.ai.infrastructure.dao.po.McpProtocolMappingPO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 协议仓储实现 —— infrastructure 层适配器
 *
 * <p>实现 domain 层定义的 IProtocolRepository 端口。
 * 一次存储包含：生成 protocolId → INSERT mcp_protocol_http → 批量 INSERT mcp_protocol_mapping。
 * 整个过程在事务保护下完成，任意一步失败则回滚所有插入。
 */
@Slf4j
@Repository
public class ProtocolRepository implements IProtocolRepository {

    @Resource
    private IMcpProtocolHttpDao protocolHttpDao;

    @Resource
    private IMcpProtocolMappingDao protocolMappingDao;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<Long> saveHttpProtocolAndMapping(List<HTTPProtocolVO> httpProtocolVOS) {
        List<Long> protocolIdList = new ArrayList<>();

        for (HTTPProtocolVO httpProtocolVO : httpProtocolVOS) {

            // 生成 8 位唯一协议 ID（commons-lang3 已引入）
            long protocolId = Long.parseLong(RandomStringUtils.randomNumeric(8));

            // 1. 保存 HTTP 协议配置表
            McpProtocolHttpPO httpPO = McpProtocolHttpPO.builder()
                    .protocolId(protocolId)
                    .httpUrl(httpProtocolVO.getHttpUrl())
                    .httpMethod(httpProtocolVO.getHttpMethod())
                    .httpHeaders(httpProtocolVO.getHttpHeaders())
                    .timeout(httpProtocolVO.getTimeout())
                    .retryTimes(3)
                    .status(ProtocolStatusEnum.ENABLE.getCode())
                    .build();
            protocolHttpDao.insert(httpPO);

            // 2. 保存协议字段映射表
            List<HTTPProtocolVO.ProtocolMapping> mappings = httpProtocolVO.getMappings();
            if (null == mappings || mappings.isEmpty()) continue;

            for (HTTPProtocolVO.ProtocolMapping mapping : mappings) {
                McpProtocolMappingPO mappingPO = McpProtocolMappingPO.builder()
                        .protocolId(protocolId)
                        .mappingType(mapping.getMappingType())
                        .parentPath(mapping.getParentPath())
                        .fieldName(mapping.getFieldName())
                        .mcpPath(mapping.getMcpPath())
                        .mcpType(mapping.getMcpType())
                        .mcpDesc(mapping.getMcpDesc())
                        .isRequired(mapping.getIsRequired())
                        .sortOrder(mapping.getSortOrder())
                        .build();
                protocolMappingDao.insert(mappingPO);
            }

            protocolIdList.add(protocolId);
            log.info("存储协议 protocolId:{} url:{} mappings:{}", protocolId, httpProtocolVO.getHttpUrl(), mappings.size());
        }

        return protocolIdList;
    }

}
