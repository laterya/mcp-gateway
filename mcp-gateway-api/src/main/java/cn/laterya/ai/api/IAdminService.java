package cn.laterya.ai.api;

import cn.laterya.ai.api.dto.*;
import cn.laterya.ai.api.response.Response;
import cn.laterya.ai.api.response.ResponsePage;

import java.util.List;

/** 运营配置管理服务接口 */
public interface IAdminService {

    // ===== 保存 =====
    Response<GatewayConfigResponseDTO> saveGatewayConfig(GatewayConfigRequestDTO.GatewayConfig requestDTO);
    Response<GatewayConfigResponseDTO> saveGatewayToolConfig(GatewayConfigRequestDTO.GatewayToolConfig requestDTO);
    Response<GatewayConfigResponseDTO> saveGatewayProtocol(GatewayConfigRequestDTO.GatewayProtocol requestDTO);
    Response<GatewayConfigResponseDTO> saveGatewayAuth(GatewayConfigRequestDTO.GatewayAuth requestDTO);

    // ===== 导入/解析协议 =====
    Response<GatewayConfigResponseDTO> importGatewayProtocol(GatewayConfigRequestDTO.GatewayProtocolImport requestDTO);
    Response<List<GatewayProtocolDTO>> analysisProtocol(GatewayConfigRequestDTO.GatewayProtocolImport requestDTO);

    // ===== 网关查询 =====
    Response<List<GatewayConfigDTO>> queryGatewayConfigList();
    ResponsePage<List<GatewayConfigDTO>> queryGatewayConfigPage(GatewayConfigQueryDTO queryDTO);

    // ===== 工具查询 =====
    Response<List<GatewayToolConfigDTO>> queryGatewayToolList();
    ResponsePage<List<GatewayToolConfigDTO>> queryGatewayToolPage(GatewayToolQueryDTO queryDTO);
    Response<List<GatewayToolConfigDTO>> queryGatewayToolListByGatewayId(String gatewayId);

    // ===== 协议查询 =====
    Response<List<GatewayProtocolDTO>> queryGatewayProtocolList();
    ResponsePage<List<GatewayProtocolDTO>> queryGatewayProtocolPage(GatewayProtocolQueryDTO queryDTO);
    Response<List<GatewayProtocolDTO>> queryGatewayProtocolListByGatewayId(String gatewayId);

    // ===== 鉴权查询 =====
    Response<List<GatewayAuthDTO>> queryGatewayAuthList();
    ResponsePage<List<GatewayAuthDTO>> queryGatewayAuthPage(GatewayAuthQueryDTO queryDTO);

    // ===== 删除 =====
    Response<GatewayConfigResponseDTO> deleteGatewayToolConfig(String gatewayId, Long toolId);
    Response<GatewayConfigResponseDTO> deleteGatewayAuth(String gatewayId);

    // ===== LLM 测试 =====
    Response<GatewayLLMTestResponseDTO> testCallGateway(GatewayLLMTestRequestDTO requestDTO);

}
