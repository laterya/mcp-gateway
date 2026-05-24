package cn.laterya.ai.domain.protocol.model.valobj.enums;

import cn.laterya.ai.types.enums.ResponseCode;
import cn.laterya.ai.types.exception.AppException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 协议状态枚举
 *
 * <p>对应 mcp_protocol_http.status 字段，控制协议配置是否生效。
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ProtocolStatusEnum {

    ENABLE(1, "启用"),
    DISABLE(0, "禁用"),

    ;

    private Integer code;
    private String info;

    public static ProtocolStatusEnum getByCode(Integer code) {
        if (null == code) return null;
        for (ProtocolStatusEnum e : values()) {
            if (e.code.equals(code)) return e;
        }
        throw new AppException(ResponseCode.ENUM_NOT_FOUND.getCode(), ResponseCode.ENUM_NOT_FOUND.getInfo());
    }

}
