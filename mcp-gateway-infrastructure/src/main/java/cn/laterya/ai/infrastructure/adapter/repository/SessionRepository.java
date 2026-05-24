package cn.laterya.ai.infrastructure.adapter.repository;

import cn.laterya.ai.domain.session.adapter.repository.ISessionRepository;
import cn.laterya.ai.domain.session.model.valobj.McpGatewayConfigVO;
import cn.laterya.ai.domain.session.model.valobj.McpToolConfigVO;
import cn.laterya.ai.domain.session.model.valobj.McpToolProtocolConfigVO;
import cn.laterya.ai.infrastructure.dao.IMcpGatewayDao;
import cn.laterya.ai.infrastructure.dao.IMcpGatewayToolDao;
import cn.laterya.ai.infrastructure.dao.IMcpProtocolHttpDao;
import cn.laterya.ai.infrastructure.dao.IMcpProtocolMappingDao;
import cn.laterya.ai.infrastructure.dao.po.McpGatewayPO;
import cn.laterya.ai.infrastructure.dao.po.McpGatewayToolPO;
import cn.laterya.ai.infrastructure.dao.po.McpProtocolHttpPO;
import cn.laterya.ai.infrastructure.dao.po.McpProtocolMappingPO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class SessionRepository implements ISessionRepository {

    @Resource
    private IMcpGatewayDao mcpGatewayDao;

    @Resource
    private IMcpGatewayToolDao mcpGatewayToolDao;

    @Resource
    private IMcpProtocolMappingDao mcpProtocolMappingDao;

    @Resource
    private IMcpProtocolHttpDao mcpProtocolHttpDao;

    @Override
    public McpGatewayConfigVO queryMcpGatewayConfigByGatewayId(String gatewayId) {
        McpGatewayPO gateway = mcpGatewayDao.queryByGatewayId(gatewayId);
        if (null == gateway) return null;

        return McpGatewayConfigVO.builder()
                .gatewayId(gateway.getGatewayId())
                .gatewayName(gateway.getGatewayName())
                .gatewayDesc(gateway.getGatewayDesc())
                .version(gateway.getVersion())
                .build();
    }

    @Override
    public List<McpToolConfigVO> queryMcpGatewayToolConfigListByGatewayId(String gatewayId) {
        List<McpGatewayToolPO> tools = mcpGatewayToolDao.queryByGatewayId(gatewayId);
        if (tools.isEmpty()) return new ArrayList<>();

        List<McpToolConfigVO> result = new ArrayList<>();
        for (McpGatewayToolPO tool : tools) {
            // 查询 request 类型的字段映射
            List<McpProtocolMappingPO> mappings = mcpProtocolMappingDao.queryByProtocolId(tool.getProtocolId());
            List<McpToolProtocolConfigVO.ProtocolMapping> requestMappings = new ArrayList<>();
            for (McpProtocolMappingPO mapping : mappings) {
                if (!"request".equals(mapping.getMappingType())) continue;
                requestMappings.add(McpToolProtocolConfigVO.ProtocolMapping.builder()
                        .parentPath(mapping.getParentPath())
                        .fieldName(mapping.getFieldName())
                        .mcpPath(mapping.getMcpPath())
                        .mcpType(mapping.getMcpType())
                        .mcpDesc(mapping.getMcpDesc())
                        .isRequired(mapping.getIsRequired())
                        .sortOrder(mapping.getSortOrder())
                        .build());
            }

            result.add(McpToolConfigVO.builder()
                    .toolId(tool.getToolId())
                    .toolName(tool.getToolName())
                    .toolDescription(tool.getToolDescription())
                    .mcpToolProtocolConfigVO(McpToolProtocolConfigVO.builder()
                            .requestProtocolMappings(requestMappings)
                            .build())
                    .build());
        }
        return result;
    }

    @Override
    public McpToolProtocolConfigVO queryMcpGatewayProtocolConfig(String gatewayId, String toolName) {
        // 1. 按 gatewayId + toolName 精确查询工具
        McpGatewayToolPO tool = mcpGatewayToolDao.queryByGatewayIdAndToolName(gatewayId, toolName);
        if (null == tool) return null;

        // 2. 查询 HTTP 协议配置
        McpProtocolHttpPO httpProtocol = mcpProtocolHttpDao.queryByProtocolId(tool.getProtocolId());
        if (null == httpProtocol) return null;

        McpToolProtocolConfigVO.HTTPConfig httpConfig = McpToolProtocolConfigVO.HTTPConfig.builder()
                .httpUrl(httpProtocol.getHttpUrl())
                .httpMethod(httpProtocol.getHttpMethod())
                .httpHeaders(httpProtocol.getHttpHeaders())
                .timeout(httpProtocol.getTimeout())
                .build();

        return McpToolProtocolConfigVO.builder()
                .httpConfig(httpConfig)
                .build();
    }

}
