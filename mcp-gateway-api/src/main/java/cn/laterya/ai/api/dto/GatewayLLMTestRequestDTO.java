package cn.laterya.ai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GatewayLLMTestRequestDTO {

    private String gatewayId;
    private String message;
    private String apiKey;
    @Builder.Default
    private Long timeout = 60L;
    @Builder.Default
    private Boolean reload = false;
}
