package cn.laterya.ai.domain.protocol.service;

import cn.laterya.ai.domain.protocol.model.entity.AnalysisCommandEntity;
import cn.laterya.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;

import java.util.List;

/**
 * 协议解析接口（DDD 中的 Port）
 *
 * <p>将 OpenAPI JSON 解析为 {@link HTTPProtocolVO} 列表，供后续协议存储服务使用。
 */
public interface IProtocolAnalysis {

    /**
     * 执行协议解析
     *
     * @param commandEntity 解析命令（包含类型、JSON 原文、目标 endpoints）
     * @return 解析后的协议对象列表
     */
    List<HTTPProtocolVO> doAnalysis(AnalysisCommandEntity commandEntity);

}
