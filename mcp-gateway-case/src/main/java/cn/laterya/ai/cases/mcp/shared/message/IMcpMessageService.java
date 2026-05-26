package cn.laterya.ai.cases.mcp.shared.message;

import cn.laterya.ai.domain.session.model.entity.HandleMessageCommandEntity;
import org.springframework.http.ResponseEntity;

/**
 * 消息处理编排接口（SSE / Streamable 共用）
 */
public interface IMcpMessageService {

    ResponseEntity<Void> handleMessage(HandleMessageCommandEntity commandEntity) throws Exception;

}
