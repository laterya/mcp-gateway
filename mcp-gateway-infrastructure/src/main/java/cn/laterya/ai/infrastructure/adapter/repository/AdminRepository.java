package cn.laterya.ai.infrastructure.adapter.repository;

import cn.laterya.ai.domain.admin.adapter.repository.IAdminRepository;
import cn.laterya.ai.domain.admin.model.entity.*;
import cn.laterya.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;
import cn.laterya.ai.infrastructure.dao.*;
import cn.laterya.ai.infrastructure.dao.po.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class AdminRepository implements IAdminRepository {

    @Resource private IMcpGatewayDao mcpGatewayDao;
    @Resource private IMcpGatewayToolDao mcpGatewayToolDao;
    @Resource private IMcpGatewayAuthDao mcpGatewayAuthDao;
    @Resource private IMcpProtocolHttpDao protocolHttpDao;
    @Resource private IMcpProtocolMappingDao protocolMappingDao;

    @Override
    public List<GatewayConfigEntity> queryGatewayConfigList() {
        return mcpGatewayDao.queryAll().stream().map(po -> GatewayConfigEntity.builder()
                .gatewayId(po.getGatewayId()).gatewayName(po.getGatewayName()).gatewayDesc(po.getGatewayDesc())
                .version(po.getVersion()).auth(po.getAuth()).status(po.getStatus()).build()).collect(Collectors.toList());
    }

    @Override
    public GatewayConfigPageEntity queryGatewayConfigPage(GatewayConfigQueryEntity q) {
        int page = q.getPage() != null ? q.getPage() : 1;
        int rows = q.getRows() != null ? q.getRows() : 10;
        int offset = (page - 1) * rows;
        long total = mcpGatewayDao.queryCount(q.getGatewayId(), q.getGatewayName());
        List<GatewayConfigEntity> list = mcpGatewayDao.queryPage(q.getGatewayId(), q.getGatewayName(), rows, offset)
                .stream().map(po -> GatewayConfigEntity.builder()
                        .gatewayId(po.getGatewayId()).gatewayName(po.getGatewayName()).gatewayDesc(po.getGatewayDesc())
                        .version(po.getVersion()).auth(po.getAuth()).status(po.getStatus()).build()).collect(Collectors.toList());
        return GatewayConfigPageEntity.builder().dataList(list).total(total).build();
    }

    @Override
    public List<GatewayToolConfigEntity> queryGatewayToolList() {
        return mcpGatewayToolDao.queryAll().stream().map(po -> GatewayToolConfigEntity.builder()
                .gatewayId(po.getGatewayId()).toolId(po.getToolId()).toolName(po.getToolName()).toolType(po.getToolType())
                .toolDescription(po.getToolDescription()).toolVersion(po.getToolVersion())
                .protocolId(po.getProtocolId()).protocolType(po.getProtocolType()).build()).collect(Collectors.toList());
    }

    @Override
    public GatewayToolPageEntity queryGatewayToolPage(GatewayToolQueryEntity q) {
        int page = q.getPage() != null ? q.getPage() : 1;
        int rows = q.getRows() != null ? q.getRows() : 10;
        int offset = (page - 1) * rows;
        long total = mcpGatewayToolDao.queryCount(q.getGatewayId(), q.getToolName());
        List<GatewayToolConfigEntity> list = mcpGatewayToolDao.queryPage(q.getGatewayId(), q.getToolName(), rows, offset)
                .stream().map(po -> GatewayToolConfigEntity.builder()
                        .gatewayId(po.getGatewayId()).toolId(po.getToolId()).toolName(po.getToolName()).toolType(po.getToolType())
                        .toolDescription(po.getToolDescription()).toolVersion(po.getToolVersion())
                        .protocolId(po.getProtocolId()).protocolType(po.getProtocolType()).build()).collect(Collectors.toList());
        return GatewayToolPageEntity.builder().dataList(list).total(total).build();
    }

    @Override
    public List<GatewayToolConfigEntity> queryGatewayToolListByGatewayId(String gatewayId) {
        return mcpGatewayToolDao.queryByGatewayId(gatewayId).stream().map(po -> GatewayToolConfigEntity.builder()
                .gatewayId(po.getGatewayId()).toolId(po.getToolId()).toolName(po.getToolName()).toolType(po.getToolType())
                .toolDescription(po.getToolDescription()).toolVersion(po.getToolVersion())
                .protocolId(po.getProtocolId()).protocolType(po.getProtocolType()).build()).collect(Collectors.toList());
    }

    @Override
    public List<GatewayProtocolConfigEntity> queryGatewayProtocolList() {
        return protocolHttpDao.queryAll().stream().map(po -> {
            List<McpProtocolMappingPO> mappings = protocolMappingDao.queryByProtocolId(po.getProtocolId());
            return GatewayProtocolConfigEntity.builder()
                    .protocolId(po.getProtocolId()).httpUrl(po.getHttpUrl()).httpMethod(po.getHttpMethod())
                    .httpHeaders(po.getHttpHeaders()).timeout(po.getTimeout())
                    .mappings(mappings == null ? null : mappings.stream().map(m -> GatewayProtocolConfigEntity.ProtocolMappingEntity.builder()
                            .mappingType(m.getMappingType()).parentPath(m.getParentPath()).fieldName(m.getFieldName())
                            .mcpPath(m.getMcpPath()).mcpType(m.getMcpType()).mcpDesc(m.getMcpDesc())
                            .isRequired(m.getIsRequired()).sortOrder(m.getSortOrder()).build()).collect(Collectors.toList()))
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public GatewayProtocolPageEntity queryGatewayProtocolPage(GatewayProtocolQueryEntity q) {
        int page = q.getPage() != null ? q.getPage() : 1;
        int rows = q.getRows() != null ? q.getRows() : 10;
        int offset = (page - 1) * rows;
        long total = protocolHttpDao.queryCount(q.getProtocolId(), q.getHttpUrl());
        List<GatewayProtocolConfigEntity> list = protocolHttpDao.queryPage(q.getProtocolId(), q.getHttpUrl(), rows, offset)
                .stream().map(po -> {
                    List<McpProtocolMappingPO> mappings = protocolMappingDao.queryByProtocolId(po.getProtocolId());
                    return GatewayProtocolConfigEntity.builder()
                            .protocolId(po.getProtocolId()).httpUrl(po.getHttpUrl()).httpMethod(po.getHttpMethod())
                            .httpHeaders(po.getHttpHeaders()).timeout(po.getTimeout())
                            .mappings(mappings == null ? null : mappings.stream().map(m -> GatewayProtocolConfigEntity.ProtocolMappingEntity.builder()
                                    .mappingType(m.getMappingType()).parentPath(m.getParentPath()).fieldName(m.getFieldName())
                                    .mcpPath(m.getMcpPath()).mcpType(m.getMcpType()).mcpDesc(m.getMcpDesc())
                                    .isRequired(m.getIsRequired()).sortOrder(m.getSortOrder()).build()).collect(Collectors.toList()))
                            .build();
                }).collect(Collectors.toList());
        return GatewayProtocolPageEntity.builder().dataList(list).total(total).build();
    }

    @Override
    public List<GatewayProtocolConfigEntity> queryGatewayProtocolListByProtocolIds(List<Long> protocolIds) {
        if (protocolIds == null || protocolIds.isEmpty()) return Collections.emptyList();
        return protocolIds.stream().distinct().map(protocolId -> {
            McpProtocolHttpPO po = protocolHttpDao.queryByProtocolId(protocolId);
            if (po == null) return null;
            List<McpProtocolMappingPO> mappings = protocolMappingDao.queryByProtocolId(protocolId);
            return GatewayProtocolConfigEntity.builder()
                    .protocolId(po.getProtocolId()).httpUrl(po.getHttpUrl()).httpMethod(po.getHttpMethod())
                    .httpHeaders(po.getHttpHeaders()).timeout(po.getTimeout())
                    .mappings(mappings == null ? null : mappings.stream().map(m -> GatewayProtocolConfigEntity.ProtocolMappingEntity.builder()
                            .mappingType(m.getMappingType()).parentPath(m.getParentPath()).fieldName(m.getFieldName())
                            .mcpPath(m.getMcpPath()).mcpType(m.getMcpType()).mcpDesc(m.getMcpDesc())
                            .isRequired(m.getIsRequired()).sortOrder(m.getSortOrder()).build()).collect(Collectors.toList()))
                    .build();
        }).filter(java.util.Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public List<GatewayAuthConfigEntity> queryGatewayAuthList() {
        return mcpGatewayAuthDao.queryAll().stream().map(po -> GatewayAuthConfigEntity.builder()
                .gatewayId(po.getGatewayId()).apiKey(po.getApiKey()).rateLimit(po.getRateLimit())
                .expireTime(po.getExpireTime()).build()).collect(Collectors.toList());
    }

    @Override
    public GatewayAuthPageEntity queryGatewayAuthPage(GatewayAuthQueryEntity q) {
        int page = q.getPage() != null ? q.getPage() : 1;
        int rows = q.getRows() != null ? q.getRows() : 10;
        int offset = (page - 1) * rows;
        long total = mcpGatewayAuthDao.queryCount(q.getGatewayId(), q.getApiKey());
        List<GatewayAuthConfigEntity> list = mcpGatewayAuthDao.queryPage(q.getGatewayId(), q.getApiKey(), rows, offset)
                .stream().map(po -> GatewayAuthConfigEntity.builder()
                        .gatewayId(po.getGatewayId()).apiKey(po.getApiKey()).rateLimit(po.getRateLimit())
                        .expireTime(po.getExpireTime()).build()).collect(Collectors.toList());
        return GatewayAuthPageEntity.builder().dataList(list).total(total).build();
    }

}
