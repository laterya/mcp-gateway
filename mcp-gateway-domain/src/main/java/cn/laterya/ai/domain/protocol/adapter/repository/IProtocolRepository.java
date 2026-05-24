package cn.laterya.ai.domain.protocol.adapter.repository;

import cn.laterya.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;
import java.util.List;

public interface IProtocolRepository {
    List<Long> saveHttpProtocolAndMapping(List<HTTPProtocolVO> httpProtocolVOS);
    void deleteByProtocolId(Long protocolId);
}
