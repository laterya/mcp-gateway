package cn.laterya.ai.domain.protocol.model.entity;

import cn.laterya.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 协议存储命令实体 —— 封装待存储的协议数据
 *
 * <p>与 {@link AnalysisCommandEntity} 形成"解析 → 存储"的命令接力：
 * AnalysisCommandEntity 驱动解析产生 List<HTTPProtocolVO>，
 * StorageCommandEntity 驱动存储消费这些 VO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StorageCommandEntity {

    /** 解析后的协议列表，每条对应一个接口端点 */
    private List<HTTPProtocolVO> httpProtocolVOS;

}
