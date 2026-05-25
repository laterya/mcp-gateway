package cn.laterya.ai.domain.session;

import cn.laterya.ai.domain.session.model.SessionConfigVO;
import cn.laterya.ai.domain.session.model.enums.TransportTypeEnum;
import org.junit.jupiter.api.Test;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Sinks;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SessionConfigVO 单元测试
 *
 * <p>验证统一 Session 模型下，两种传输方式的 Session 行为一致：
 * - SSE 传输：sink 非空，有推送能力
 * - Streamable HTTP 传输：sink 为空，过期/活跃判断不受影响
 */
public class SessionConfigVOTest {

    @Test
    public void test_sseSession_hasSink() {
        Sinks.Many<ServerSentEvent<String>> sink = Sinks.many().multicast().onBackpressureBuffer();
        SessionConfigVO session = new SessionConfigVO("sess-1", sink, TransportTypeEnum.SSE);

        assertEquals("sess-1", session.getSessionId());
        assertNotNull(session.getSink());
        assertEquals(TransportTypeEnum.SSE, session.getTransportType());
        assertTrue(session.isActive());
    }

    @Test
    public void test_streamableHttpSession_sinkIsNull() {
        SessionConfigVO session = new SessionConfigVO("sess-2", null, TransportTypeEnum.STREAMABLE_HTTP);

        assertEquals("sess-2", session.getSessionId());
        assertNull(session.getSink());
        assertEquals(TransportTypeEnum.STREAMABLE_HTTP, session.getTransportType());
        assertTrue(session.isActive());
    }

    @Test
    public void test_isExpired_sameForBothTransports() {
        SessionConfigVO sseSession = new SessionConfigVO("sess-3",
                Sinks.many().multicast().onBackpressureBuffer(), TransportTypeEnum.SSE);
        SessionConfigVO httpSession = new SessionConfigVO("sess-4", null, TransportTypeEnum.STREAMABLE_HTTP);

        // 新创建的 session 都不应过期
        assertFalse(sseSession.isExpired(30));
        assertFalse(httpSession.isExpired(30));
    }

    @Test
    public void test_markInactive_sameForBothTransports() {
        SessionConfigVO sseSession = new SessionConfigVO("sess-5",
                Sinks.many().multicast().onBackpressureBuffer(), TransportTypeEnum.SSE);
        SessionConfigVO httpSession = new SessionConfigVO("sess-6", null, TransportTypeEnum.STREAMABLE_HTTP);

        sseSession.markInactive();
        httpSession.markInactive();

        assertFalse(sseSession.isActive());
        assertFalse(httpSession.isActive());
    }

}
