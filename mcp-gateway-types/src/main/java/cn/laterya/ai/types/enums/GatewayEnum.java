package cn.laterya.ai.types.enums;

import cn.laterya.ai.types.exception.AppException;
import lombok.Getter;

/**
 * 网关枚举 —— 放置在 types 层供 gateway/auth/protocol 多限界上下文共用
 */
@Getter
public enum GatewayEnum {

    ;

    /** 网关鉴权模式 */
    @Getter
    public enum GatewayStatus {

        NOT_VERIFIED(0, "不校验"),
        STRONG_VERIFIED(1, "强校验"),
        ;

        private final Integer code;
        private final String info;

        GatewayStatus(Integer code, String info) {
            this.code = code;
            this.info = info;
        }

        public static GatewayStatus get(Integer code) {
            if (code == null) return null;
            for (GatewayStatus val : values()) {
                if (val.code.equals(code)) return val;
            }
            throw new AppException(ResponseCode.ENUM_NOT_FOUND.getCode(), ResponseCode.ENUM_NOT_FOUND.getInfo());
        }
    }

    /** 认证状态（对应 mcp_gateway.auth 字段） */
    @Getter
    public enum GatewayAuthStatusEnum {

        ENABLE(1, "启用"),
        DISABLE(0, "禁用"),
        ;

        private final Integer code;
        private final String info;

        GatewayAuthStatusEnum(Integer code, String info) {
            this.code = code;
            this.info = info;
        }

        public static GatewayAuthStatusEnum getByCode(Integer code) {
            if (null == code) return null;
            for (GatewayAuthStatusEnum e : values()) {
                if (e.getCode().equals(code)) return e;
            }
            throw new AppException(ResponseCode.ENUM_NOT_FOUND.getCode(), ResponseCode.ENUM_NOT_FOUND.getInfo());
        }
    }

}
