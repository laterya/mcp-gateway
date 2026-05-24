package cn.laterya.ai.domain.protocol.model.valobj.enums;

import cn.laterya.ai.types.enums.ResponseCode;
import cn.laterya.ai.types.exception.AppException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 协议解析类型枚举 —— 充血模型
 *
 * <p>嵌套 {@link SwaggerAnalysisAction} 将"判断用哪个策略"的逻辑内聚到枚举中。
 * 按 DDD 充血原则：谁定义类型，谁负责提供类型判断方法，避免逻辑散落在 Service 里。
 *
 * <p>分发机制：枚举 code 值（如 "requestBodyAnalysis"）与 Spring Bean 名一致，
 * ProtocolAnalysis 通过 {@code Map<String, IProtocolAnalysisStrategy>} 实现零配置策略分发。
 */
@Getter
public enum AnalysisTypeEnum {

    swagger,

    ;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public enum SwaggerAnalysisAction {

        requestBodyAnalysis("requestBodyAnalysis", "解析对象"),
        parametersAnalysis("parametersAnalysis", "解析属性"),

        ;

        private String code;
        private String info;

        /**
         * 充血判断：返回 operation 节点适用的所有解析策略
         *
         * <p>一个 operation 可能同时包含 requestBody 和 parameters
         * （如 POST /endpoint?sessionId=xxx + JSON body），两者都需解析。
         *
         * @param operation OpenAPI paths 下某个 endpoint 的具体方法节点（post/get/...）
         * @return 适用的策略列表，不存在任何合法策略时抛出异常
         */
        public static List<SwaggerAnalysisAction> getApplicableStrategies(JsonNode operation) {
            List<SwaggerAnalysisAction> actions = new ArrayList<>();
            if (operation.has("requestBody")) {
                actions.add(requestBodyAnalysis);
            }
            if (operation.has("parameters")) {
                actions.add(parametersAnalysis);
            }
            if (actions.isEmpty()) {
                throw new AppException(ResponseCode.ENUM_NOT_FOUND.getCode(), ResponseCode.ENUM_NOT_FOUND.getInfo());
            }
            return actions;
        }

    }

}
