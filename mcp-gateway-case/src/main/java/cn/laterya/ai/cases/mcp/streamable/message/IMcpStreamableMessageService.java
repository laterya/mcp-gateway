package cn.laterya.ai.cases.mcp.streamable.message;

import cn.laterya.ai.domain.session.model.entity.HandleMessageCommandEntity;
import org.springframework.http.ResponseEntity;

/**
 * Streamable HTTP — 消息处理编排接口
 */
public interface IMcpStreamableMessageService {

    ResponseEntity<Void> handleMessage(HandleMessageCommandEntity commandEntity) throws Exception;

}
