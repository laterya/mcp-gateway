package cn.laterya.ai.types.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

    private final String code;

    public AppException(String code, String message) {
        super(message);
        this.code = code;
    }

    public AppException(int code, String message) {
        super(message);
        this.code = String.valueOf(code);
    }

}
