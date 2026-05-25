package cn.laterya.ai.domain.auth.model.valobj.enums;

import cn.laterya.ai.types.enums.ResponseCode;
import cn.laterya.ai.types.exception.AppException;
import lombok.Getter;

/**
 * 鉴权状态枚举 —— 使用嵌套枚举将两类状态隔离在同一命名空间下
 *
 * GatewayConfig：网关级别的鉴权模式（是否启用认证）
 * AuthConfig：单条认证记录的状态（启用/禁用）
 */
public enum AuthStatusEnum {

    ;

    @Getter
    public enum GatewayConfig {

        NOT_VERIFIED(0, "不校验"),
        STRONG_VERIFIED(1, "强校验"),
        ;

        private final Integer code;
        private final String info;

        GatewayConfig(Integer code, String info) {
            this.code = code;
            this.info = info;
        }

        public static GatewayConfig get(Integer code) {
            if (code == null) return null;
            for (GatewayConfig val : values()) {
                if (val.code.equals(code)) {
                    return val;
                }
            }
            throw new AppException(ResponseCode.ENUM_NOT_FOUND.getCode(), ResponseCode.ENUM_NOT_FOUND.getInfo());
        }
    }

    @Getter
    public enum AuthConfig {

        DISABLE(0, "禁用"),
        ENABLE(1, "启用"),
        ;

        private final Integer code;
        private final String info;

        AuthConfig(Integer code, String info) {
            this.code = code;
            this.info = info;
        }

        public static AuthConfig get(Integer code) {
            if (code == null) return null;
            for (AuthConfig val : values()) {
                if (val.code.equals(code)) {
                    return val;
                }
            }
            throw new AppException(ResponseCode.ENUM_NOT_FOUND.getCode(), ResponseCode.ENUM_NOT_FOUND.getInfo());
        }
    }

}
