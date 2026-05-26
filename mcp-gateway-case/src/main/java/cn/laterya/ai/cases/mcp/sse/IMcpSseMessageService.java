package cn.laterya.ai.cases.mcp.sse;

import cn.laterya.ai.domain.session.model.entity.HandleMessageCommandEntity;
import org.springframework.http.ResponseEntity;

/**
 * SSE 传输 — 消息处理编排接口
 */
public interface IMcpSseMessageService {

    ResponseEntity<Void> handleMessage(HandleMessageCommandEntity commandEntity) throws Exception;

}
