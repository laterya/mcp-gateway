package cn.laterya.ai.api;

import cn.laterya.ai.api.dto.GatewayConfigDTO;
import cn.laterya.ai.api.dto.GatewayConfigRequestDTO;
import cn.laterya.ai.api.dto.GatewayConfigResponseDTO;
import cn.laterya.ai.api.response.Response;

import java.util.List;

/** 运营配置管理服务接口 */
public interface IAdminService {

    Response<GatewayConfigResponseDTO> saveGatewayConfig(GatewayConfigRequestDTO.GatewayConfig requestDTO);

    Response<GatewayConfigResponseDTO> saveGatewayToolConfig(GatewayConfigRequestDTO.GatewayToolConfig requestDTO);

    Response<GatewayConfigResponseDTO> saveGatewayProtocol(GatewayConfigRequestDTO.GatewayProtocol requestDTO);

    Response<GatewayConfigResponseDTO> saveGatewayAuth(GatewayConfigRequestDTO.GatewayAuth requestDTO);

    Response<List<GatewayConfigDTO>> queryGatewayConfigList();

}
