package cn.laterya.ai.trigger.http;

import cn.laterya.ai.api.IAdminService;
import cn.laterya.ai.api.dto.*;
import cn.laterya.ai.api.response.Response;
import cn.laterya.ai.api.response.ResponsePage;
import cn.laterya.ai.cases.admin.IAdminLLMService;
import cn.laterya.ai.cases.admin.IAdminOrchestrationService;
import cn.laterya.ai.domain.admin.model.entity.*;
import cn.laterya.ai.domain.auth.model.entity.RegisterCommandEntity;
import cn.laterya.ai.domain.gateway.model.entity.GatewayConfigCommandEntity;
import cn.laterya.ai.domain.gateway.model.entity.GatewayToolConfigCommandEntity;
import cn.laterya.ai.domain.gateway.model.valobj.GatewayConfigVO;
import cn.laterya.ai.domain.gateway.model.valobj.GatewayToolConfigVO;
import cn.laterya.ai.domain.protocol.model.entity.AnalysisCommandEntity;
import cn.laterya.ai.domain.protocol.model.entity.StorageCommandEntity;
import cn.laterya.ai.domain.protocol.model.valobj.enums.AnalysisTypeEnum;
import cn.laterya.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;
import cn.laterya.ai.domain.protocol.service.IProtocolAnalysis;
import cn.laterya.ai.domain.protocol.service.IProtocolStorage;
import cn.laterya.ai.types.enums.GatewayEnum;
import cn.laterya.ai.types.enums.ResponseCode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@RequestMapping("/admin/")
public class AdminController implements IAdminService {

    @Resource private IAdminOrchestrationService adminOrchestrationService;
    @Resource private IAdminLLMService adminLLMService;
    @Resource private IProtocolAnalysis protocolAnalysis;
    @Resource private IProtocolStorage protocolStorage;

    // ==================== 保存 ====================

    @PostMapping("save_gateway_config")
    public Response<GatewayConfigResponseDTO> saveGatewayConfig(@RequestBody GatewayConfigRequestDTO.GatewayConfig req) {
        try {
            log.info("保存网关配置 gatewayId:{}", req.getGatewayId());
            adminOrchestrationService.saveGatewayConfig(GatewayConfigCommandEntity.builder()
                    .gatewayConfigVO(GatewayConfigVO.builder()
                            .gatewayId(req.getGatewayId()).gatewayName(req.getGatewayName())
                            .gatewayDesc(req.getGatewayDesc()).version(req.getVersion())
                            .auth(GatewayEnum.GatewayAuthStatusEnum.getByCode(req.getAuth()))
                            .status(GatewayEnum.GatewayStatus.get(req.getStatus())).build()).build());
            return ok();
        } catch (Exception e) { return fail(e, "保存网关配置"); }
    }

    @PostMapping("save_gateway_tool_config")
    public Response<GatewayConfigResponseDTO> saveGatewayToolConfig(@RequestBody GatewayConfigRequestDTO.GatewayToolConfig req) {
        try {
            log.info("保存工具配置 gatewayId:{}", req.getGatewayId());
            adminOrchestrationService.saveGatewayToolConfig(GatewayToolConfigCommandEntity.builder()
                    .gatewayToolConfigVO(GatewayToolConfigVO.builder()
                            .gatewayId(req.getGatewayId()).toolId(req.getToolId()).toolName(req.getToolName())
                            .toolType(req.getToolType()).toolDescription(req.getToolDescription())
                            .toolVersion(req.getToolVersion()).protocolId(req.getProtocolId())
                            .protocolType(req.getProtocolType()).build()).build());
            return ok();
        } catch (Exception e) { return fail(e, "保存工具配置"); }
    }

    @PostMapping("save_gateway_protocol")
    public Response<GatewayConfigResponseDTO> saveGatewayProtocol(@RequestBody GatewayConfigRequestDTO.GatewayProtocol req) {
        try {
            log.info("保存协议配置");
            adminOrchestrationService.saveGatewayProtocol(toStorageCommand(req.getHttpProtocols()));
            return ok();
        } catch (Exception e) { return fail(e, "保存协议配置"); }
    }

    @PostMapping("save_gateway_auth")
    public Response<GatewayConfigResponseDTO> saveGatewayAuth(@RequestBody GatewayConfigRequestDTO.GatewayAuth req) {
        try {
            log.info("保存鉴权 gatewayId:{}", req.getGatewayId());
            adminOrchestrationService.saveGatewayAuth(RegisterCommandEntity.builder()
                    .gatewayId(req.getGatewayId()).rateLimit(req.getRateLimit()).expireTime(req.getExpireTime()).build());
            return ok();
        } catch (Exception e) { return fail(e, "保存鉴权"); }
    }

    // ==================== 导入/解析协议 ====================

    @PostMapping("import_gateway_protocol")
    public Response<GatewayConfigResponseDTO> importGatewayProtocol(@RequestBody GatewayConfigRequestDTO.GatewayProtocolImport req) {
        try {
            log.info("导入协议 endpoints:{}", req.getEndpoints());
            List<HTTPProtocolVO> vos = protocolAnalysis.doAnalysis(AnalysisCommandEntity.builder()
                    .type(AnalysisTypeEnum.swagger).openApiJson(req.getOpenApiJson()).endpoints(req.getEndpoints()).build());
            protocolStorage.doStorage(StorageCommandEntity.builder().httpProtocolVOS(vos).build());
            return ok();
        } catch (Exception e) { return fail(e, "导入协议"); }
    }

    @PostMapping("analysis_protocol")
    public Response<List<GatewayProtocolDTO>> analysisProtocol(@RequestBody GatewayConfigRequestDTO.GatewayProtocolImport req) {
        try {
            log.info("解析协议预览 endpoints:{}", req.getEndpoints());
            List<HTTPProtocolVO> vos = protocolAnalysis.doAnalysis(AnalysisCommandEntity.builder()
                    .type(AnalysisTypeEnum.swagger).openApiJson(req.getOpenApiJson()).endpoints(req.getEndpoints()).build());
            List<GatewayProtocolDTO> dtos = vos.stream().map(v -> {
                GatewayProtocolDTO dto = GatewayProtocolDTO.builder()
                        .protocolId(v.getProtocolId()).httpUrl(v.getHttpUrl()).httpMethod(v.getHttpMethod())
                        .httpHeaders(v.getHttpHeaders()).timeout(v.getTimeout()).build();
                if (v.getMappings() != null) dto.setMappings(v.getMappings().stream().map(m -> GatewayProtocolDTO.ProtocolMappingDTO.builder()
                        .mappingType(m.getMappingType()).parentPath(m.getParentPath()).fieldName(m.getFieldName())
                        .mcpPath(m.getMcpPath()).mcpType(m.getMcpType()).mcpDesc(m.getMcpDesc())
                        .isRequired(m.getIsRequired()).sortOrder(m.getSortOrder()).build()).collect(Collectors.toList()));
                return dto;
            }).collect(Collectors.toList());
            return Response.<List<GatewayProtocolDTO>>builder().code(ResponseCode.SUCCESS.getCode()).info(ResponseCode.SUCCESS.getInfo()).data(dtos).build();
        } catch (Exception e) { return failResp(e, "解析协议"); }
    }

    // ==================== 查询 ====================

    @GetMapping("query_gateway_config_list")
    public Response<List<GatewayConfigDTO>> queryGatewayConfigList() {
        try {
            List<GatewayConfigEntity> list = adminOrchestrationService.queryGatewayConfigList();
            return Response.<List<GatewayConfigDTO>>builder().code(ResponseCode.SUCCESS.getCode()).info(ResponseCode.SUCCESS.getInfo())
                    .data(list.stream().map(e -> GatewayConfigDTO.builder().gatewayId(e.getGatewayId()).gatewayName(e.getGatewayName())
                            .gatewayDesc(e.getGatewayDesc()).version(e.getVersion()).auth(e.getAuth()).status(e.getStatus()).build())
                            .collect(Collectors.toList())).build();
        } catch (Exception e) { return failResp(e, "查询网关列表"); }
    }

    @PostMapping("query_gateway_config_page")
    public ResponsePage<List<GatewayConfigDTO>> queryGatewayConfigPage(@RequestBody GatewayConfigQueryDTO q) {
        try {
            GatewayConfigPageEntity p = adminOrchestrationService.queryGatewayConfigPage(GatewayConfigQueryEntity.builder()
                    .gatewayId(q.getGatewayId()).gatewayName(q.getGatewayName()).page(q.getPage()).rows(q.getRows()).build());
            List<GatewayConfigDTO> dtos = p.getDataList().stream().map(e -> GatewayConfigDTO.builder()
                    .gatewayId(e.getGatewayId()).gatewayName(e.getGatewayName()).gatewayDesc(e.getGatewayDesc())
                    .version(e.getVersion()).auth(e.getAuth()).status(e.getStatus()).build()).collect(Collectors.toList());
            return ResponsePage.<List<GatewayConfigDTO>>builder().code(ResponseCode.SUCCESS.getCode()).info(ResponseCode.SUCCESS.getInfo()).data(dtos).total(p.getTotal()).build();
        } catch (Exception e) { return failPage(e, "查询网关分页"); }
    }

    @GetMapping("query_gateway_tool_list")
    public Response<List<GatewayToolConfigDTO>> queryGatewayToolList() {
        try {
            List<GatewayToolConfigEntity> list = adminOrchestrationService.queryGatewayToolList();
            return Response.<List<GatewayToolConfigDTO>>builder().code(ResponseCode.SUCCESS.getCode()).info(ResponseCode.SUCCESS.getInfo())
                    .data(list.stream().map(this::toToolDTO).collect(Collectors.toList())).build();
        } catch (Exception e) { return failResp(e, "查询工具列表"); }
    }

    @PostMapping("query_gateway_tool_page")
    public ResponsePage<List<GatewayToolConfigDTO>> queryGatewayToolPage(@RequestBody GatewayToolQueryDTO q) {
        try {
            GatewayToolPageEntity p = adminOrchestrationService.queryGatewayToolPage(GatewayToolQueryEntity.builder()
                    .gatewayId(q.getGatewayId()).toolName(q.getToolName()).page(q.getPage()).rows(q.getRows()).build());
            return ResponsePage.<List<GatewayToolConfigDTO>>builder().code(ResponseCode.SUCCESS.getCode()).info(ResponseCode.SUCCESS.getInfo())
                    .data(p.getDataList().stream().map(this::toToolDTO).collect(Collectors.toList())).total(p.getTotal()).build();
        } catch (Exception e) { return failPage(e, "查询工具分页"); }
    }

    @GetMapping("query_gateway_tool_list_by_gateway_id")
    public Response<List<GatewayToolConfigDTO>> queryGatewayToolListByGatewayId(@RequestParam String gatewayId) {
        try {
            List<GatewayToolConfigEntity> list = adminOrchestrationService.queryGatewayToolListByGatewayId(gatewayId);
            return Response.<List<GatewayToolConfigDTO>>builder().code(ResponseCode.SUCCESS.getCode()).info(ResponseCode.SUCCESS.getInfo())
                    .data(list.stream().map(this::toToolDTO).collect(Collectors.toList())).build();
        } catch (Exception e) { return failResp(e, "查询网关工具"); }
    }

    @GetMapping("query_gateway_protocol_list")
    public Response<List<GatewayProtocolDTO>> queryGatewayProtocolList() {
        try {
            return Response.<List<GatewayProtocolDTO>>builder().code(ResponseCode.SUCCESS.getCode()).info(ResponseCode.SUCCESS.getInfo())
                    .data(adminOrchestrationService.queryGatewayProtocolList().stream().map(this::toProtocolDTO).collect(Collectors.toList())).build();
        } catch (Exception e) { return failResp(e, "查询协议列表"); }
    }

    @PostMapping("query_gateway_protocol_page")
    public ResponsePage<List<GatewayProtocolDTO>> queryGatewayProtocolPage(@RequestBody GatewayProtocolQueryDTO q) {
        try {
            GatewayProtocolPageEntity p = adminOrchestrationService.queryGatewayProtocolPage(GatewayProtocolQueryEntity.builder()
                    .protocolId(q.getProtocolId()).httpUrl(q.getHttpUrl()).page(q.getPage()).rows(q.getRows()).build());
            return ResponsePage.<List<GatewayProtocolDTO>>builder().code(ResponseCode.SUCCESS.getCode()).info(ResponseCode.SUCCESS.getInfo())
                    .data(p.getDataList().stream().map(this::toProtocolDTO).collect(Collectors.toList())).total(p.getTotal()).build();
        } catch (Exception e) { return failPage(e, "查询协议分页"); }
    }

    @GetMapping("query_gateway_protocol_list_by_gateway_id")
    public Response<List<GatewayProtocolDTO>> queryGatewayProtocolListByGatewayId(@RequestParam String gatewayId) {
        try {
            return Response.<List<GatewayProtocolDTO>>builder().code(ResponseCode.SUCCESS.getCode()).info(ResponseCode.SUCCESS.getInfo())
                    .data(adminOrchestrationService.queryGatewayProtocolListByGatewayId(gatewayId).stream().map(this::toProtocolDTO).collect(Collectors.toList())).build();
        } catch (Exception e) { return failResp(e, "查询网关协议"); }
    }

    @GetMapping("query_gateway_auth_list")
    public Response<List<GatewayAuthDTO>> queryGatewayAuthList() {
        try {
            return Response.<List<GatewayAuthDTO>>builder().code(ResponseCode.SUCCESS.getCode()).info(ResponseCode.SUCCESS.getInfo())
                    .data(adminOrchestrationService.queryGatewayAuthList().stream().map(this::toAuthDTO).collect(Collectors.toList())).build();
        } catch (Exception e) { return failResp(e, "查询鉴权列表"); }
    }

    @PostMapping("query_gateway_auth_page")
    public ResponsePage<List<GatewayAuthDTO>> queryGatewayAuthPage(@RequestBody GatewayAuthQueryDTO q) {
        try {
            GatewayAuthPageEntity p = adminOrchestrationService.queryGatewayAuthPage(GatewayAuthQueryEntity.builder()
                    .gatewayId(q.getGatewayId()).apiKey(q.getApiKey()).page(q.getPage()).rows(q.getRows()).build());
            return ResponsePage.<List<GatewayAuthDTO>>builder().code(ResponseCode.SUCCESS.getCode()).info(ResponseCode.SUCCESS.getInfo())
                    .data(p.getDataList().stream().map(this::toAuthDTO).collect(Collectors.toList())).total(p.getTotal()).build();
        } catch (Exception e) { return failPage(e, "查询鉴权分页"); }
    }

    // ==================== 删除 ====================

    @PostMapping("delete_gateway_tool_config")
    public Response<GatewayConfigResponseDTO> deleteGatewayToolConfig(@RequestParam String gatewayId, @RequestParam Long toolId) {
        try {
            log.info("删除工具 gatewayId:{} toolId:{}", gatewayId, toolId);
            return ok();
        } catch (Exception e) { return fail(e, "删除工具"); }
    }

    @PostMapping("delete_gateway_auth")
    public Response<GatewayConfigResponseDTO> deleteGatewayAuth(@RequestParam String gatewayId) {
        try {
            log.info("删除鉴权 gatewayId:{}", gatewayId);
            adminOrchestrationService.deleteGatewayAuth(gatewayId);
            return ok();
        } catch (Exception e) { return fail(e, "删除鉴权"); }
    }

    @PostMapping("delete_gateway_protocol")
    public Response<GatewayConfigResponseDTO> deleteGatewayProtocol(@RequestParam Long protocolId) {
        try {
            log.info("删除协议 protocolId:{}", protocolId);
            adminOrchestrationService.deleteGatewayProtocol(protocolId);
            return ok();
        } catch (Exception e) { return fail(e, "删除协议"); }
    }


    @PostMapping("update_gateway_auth")
    public Response<GatewayConfigResponseDTO> updateGatewayAuth(@RequestParam String gatewayId, @RequestParam Integer rateLimit, @RequestParam(required = false) String expireTime) {
        try {
            log.info("更新鉴权 gatewayId:{} rateLimit:{}", gatewayId, rateLimit);
            adminOrchestrationService.updateGatewayAuth(gatewayId, rateLimit, expireTime);
            return ok();
        } catch (Exception e) { return fail(e, "更新鉴权"); }
    }
    // ==================== LLM 测试 ====================

    @PostMapping("test_call_gateway")
    @Override
    public Response<GatewayLLMTestResponseDTO> testCallGateway(@RequestBody GatewayLLMTestRequestDTO req) {
        try {
            log.info("测试调用网关 gatewayId:{} message:{}", req.getGatewayId(), req.getMessage());
            String content = adminLLMService.testCallGateway(
                    req.getGatewayId(),
                    req.getMessage(),
                    req.getApiKey(),
                    req.getTimeout() != null ? req.getTimeout() : 60L,
                    req.getReload() != null && req.getReload());
            return Response.<GatewayLLMTestResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode()).info(ResponseCode.SUCCESS.getInfo())
                    .data(GatewayLLMTestResponseDTO.builder().content(content).success(true).build())
                    .build();
        } catch (Exception e) {
            log.error("测试调用网关失败 gatewayId:{}", req.getGatewayId(), e);
            return Response.<GatewayLLMTestResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode()).info(e.getMessage())
                    .data(GatewayLLMTestResponseDTO.builder().success(false).content(e.getMessage()).build())
                    .build();
        }
    }

    // ==================== helpers ====================

    private StorageCommandEntity toStorageCommand(java.util.List<GatewayConfigRequestDTO.GatewayProtocol.HTTPProtocol> list) {
        StorageCommandEntity cmd = new StorageCommandEntity();
        if (list != null) cmd.setHttpProtocolVOS(list.stream().map(p -> {
            HTTPProtocolVO vo = new HTTPProtocolVO();
            vo.setProtocolId(p.getProtocolId()); vo.setHttpUrl(p.getHttpUrl()); vo.setHttpHeaders(p.getHttpHeaders());
            vo.setHttpMethod(p.getHttpMethod()); vo.setTimeout(p.getTimeout());
            if (p.getMappings() != null) vo.setMappings(p.getMappings().stream().map(m -> HTTPProtocolVO.ProtocolMapping.builder()
                    .mappingType(m.getMappingType()).parentPath(m.getParentPath()).fieldName(m.getFieldName())
                    .mcpPath(m.getMcpPath()).mcpType(m.getMcpType()).mcpDesc(m.getMcpDesc())
                    .isRequired(m.getIsRequired()).sortOrder(m.getSortOrder()).build()).collect(Collectors.toList()));
            return vo;
        }).collect(Collectors.toList()));
        return cmd;
    }

    private GatewayToolConfigDTO toToolDTO(GatewayToolConfigEntity e) {
        return GatewayToolConfigDTO.builder().gatewayId(e.getGatewayId()).toolId(e.getToolId()).toolName(e.getToolName())
                .toolType(e.getToolType()).toolDescription(e.getToolDescription()).toolVersion(e.getToolVersion())
                .protocolId(e.getProtocolId()).protocolType(e.getProtocolType()).build();
    }

    private GatewayProtocolDTO toProtocolDTO(GatewayProtocolConfigEntity e) {
        GatewayProtocolDTO dto = GatewayProtocolDTO.builder().protocolId(e.getProtocolId()).httpUrl(e.getHttpUrl())
                .httpMethod(e.getHttpMethod()).httpHeaders(e.getHttpHeaders()).timeout(e.getTimeout()).build();
        if (e.getMappings() != null) dto.setMappings(e.getMappings().stream().map(m -> GatewayProtocolDTO.ProtocolMappingDTO.builder()
                .mappingType(m.getMappingType()).parentPath(m.getParentPath()).fieldName(m.getFieldName())
                .mcpPath(m.getMcpPath()).mcpType(m.getMcpType()).mcpDesc(m.getMcpDesc())
                .isRequired(m.getIsRequired()).sortOrder(m.getSortOrder()).build()).collect(Collectors.toList()));
        return dto;
    }

    private GatewayAuthDTO toAuthDTO(GatewayAuthConfigEntity e) {
        return GatewayAuthDTO.builder().gatewayId(e.getGatewayId()).apiKey(e.getApiKey()).rateLimit(e.getRateLimit()).expireTime(e.getExpireTime()).build();
    }

    private Response<GatewayConfigResponseDTO> ok() {
        return Response.<GatewayConfigResponseDTO>builder().code(ResponseCode.SUCCESS.getCode()).info(ResponseCode.SUCCESS.getInfo())
                .data(GatewayConfigResponseDTO.builder().success(true).build()).build();
    }

    private Response<GatewayConfigResponseDTO> fail(Exception e, String op) {
        log.error("{}失败", op, e);
        return Response.<GatewayConfigResponseDTO>builder().code(ResponseCode.UN_ERROR.getCode()).info(ResponseCode.UN_ERROR.getInfo()).build();
    }

    private <T> Response<T> failResp(Exception e, String op) {
        log.error("{}失败", op, e);
        return Response.<T>builder().code(ResponseCode.UN_ERROR.getCode()).info(ResponseCode.UN_ERROR.getInfo()).build();
    }

    private <T> ResponsePage<T> failPage(Exception e, String op) {
        log.error("{}失败", op, e);
        return ResponsePage.<T>builder().code(ResponseCode.UN_ERROR.getCode()).info(ResponseCode.UN_ERROR.getInfo()).data(null).total(0L).build();
    }

}
