package cn.laterya.ai.domain.protocol.service;

import cn.laterya.ai.domain.protocol.model.entity.StorageCommandEntity;

import java.util.List;

/**
 * 协议存储接口（DDD 中的 Port）
 *
 * <p>接收解析后的协议数据，委托仓储层完成持久化。
 * 与 IProtocolAnalysis 分属不同职责：一个解析、一个持久化。
 */
public interface IProtocolStorage {

    /**
     * 存储协议配置及字段映射
     *
     * @param commandEntity 包含解析后 HTTPProtocolVO 列表的命令实体
     * @return 新生成的协议 ID 列表
     */
    List<Long> doStorage(StorageCommandEntity commandEntity);

}
