package cn.laterya.ai.domain.admin.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class GatewayAuthPageEntity {
    private List<GatewayAuthConfigEntity> dataList;
    private Long total;
}
