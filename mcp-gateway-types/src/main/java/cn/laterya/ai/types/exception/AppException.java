package cn.laterya.ai.types.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

    private final String code;
    private final int intCode;

    public AppException(String code, String message) {
        super(message);
        this.code = code;
        this.intCode = 0;
    }

    public AppException(int code, String message) {
        super(message);
        this.code = String.valueOf(code);
        this.intCode = code;
    }

}
