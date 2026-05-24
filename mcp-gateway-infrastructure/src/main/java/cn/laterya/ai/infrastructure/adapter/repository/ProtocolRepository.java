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
        for (HTTPProtocolVO vo : httpProtocolVOS) {
            long protocolId = Long.parseLong(RandomStringUtils.randomNumeric(8));
            protocolHttpDao.insert(McpProtocolHttpPO.builder()
                    .protocolId(protocolId).httpUrl(vo.getHttpUrl()).httpMethod(vo.getHttpMethod())
                    .httpHeaders(vo.getHttpHeaders()).timeout(vo.getTimeout()).retryTimes(3)
                    .status(ProtocolStatusEnum.ENABLE.getCode()).build());
            List<HTTPProtocolVO.ProtocolMapping> mappings = vo.getMappings();
            if (mappings != null) for (HTTPProtocolVO.ProtocolMapping m : mappings)
                protocolMappingDao.insert(McpProtocolMappingPO.builder()
                        .protocolId(protocolId).mappingType(m.getMappingType()).parentPath(m.getParentPath())
                        .fieldName(m.getFieldName()).mcpPath(m.getMcpPath()).mcpType(m.getMcpType())
                        .mcpDesc(m.getMcpDesc()).isRequired(m.getIsRequired()).sortOrder(m.getSortOrder()).build());
            protocolIdList.add(protocolId);
        }
        return protocolIdList;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteByProtocolId(Long protocolId) {
        protocolMappingDao.deleteByProtocolId(protocolId);
        protocolHttpDao.deleteByProtocolId(protocolId);
    }
}
