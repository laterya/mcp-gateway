package cn.laterya.ai.domain.admin.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class GatewayConfigPageEntity {
    private List<GatewayConfigEntity> dataList;
    private Long total;
}
