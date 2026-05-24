package cn.laterya.ai.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ResponsePage<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private String code;
    private String info;
    private T data;
    private Long total;
}
