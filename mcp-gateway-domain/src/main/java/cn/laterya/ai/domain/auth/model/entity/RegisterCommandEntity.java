package cn.laterya.ai.domain.auth.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterCommandEntity {

    private String gatewayId;
    private Integer rateLimit;
    private LocalDateTime expireTime;

}
