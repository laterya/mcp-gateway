package cn.laterya.ai.domain.protocol.service.analysis.strategy;

import cn.laterya.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * 协议解析策略接口
 *
 * <p>两种入参类型分别对应不同策略实现：
 * {@code RequestBodyAnalysisStrategy} 解析对象入参（$ref 引用），
 * {@code ParametersAnalysisStrategy} 解析属性入参（query/path 参数）。
 */
public interface IProtocolAnalysisStrategy {

    /**
     * 执行解析，结果追加到 mappings 列表中
     *
     * @param operation   OpenAPI 中某个 endpoint 的具体方法节点
     * @param schemas     components/schemas 节点，用于解析 $ref 引用
     * @param mappings    解析结果追加到此列表
     */
    void doAnalysis(JsonNode operation, JsonNode schemas, List<HTTPProtocolVO.ProtocolMapping> mappings);

}
