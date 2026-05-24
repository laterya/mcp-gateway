package cn.laterya.ai.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {

    SUCCESS("0000", "成功"),
    UN_ERROR("0001", "未知失败"),
    ILLEGAL_PARAMETER("0002", "非法参数"),
    METHOD_NOT_FOUND("0003", "方法未找到"),
    ENUM_NOT_FOUND("0004", "枚举未找到"),
    DB_UPDATE_FAIL("0005", "数据库更新失败"),
    ;

    private final String code;
    private final String info;

}
