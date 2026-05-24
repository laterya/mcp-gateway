package cn.laterya.ai.domain.auth;

import cn.laterya.ai.domain.auth.model.entity.RateLimitCommandEntity;
import cn.laterya.ai.domain.auth.model.entity.RegisterCommandEntity;
import cn.laterya.ai.domain.auth.service.IAuthRateLimitService;
import cn.laterya.ai.domain.auth.service.IAuthRegisterService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class AuthRateLimitServiceTest {

    @Resource
    private IAuthRateLimitService authRateLimitService;

    @Resource
    private IAuthRegisterService authRegisterService;

    @Test
    public void test_rateLimit_notLimited() {
        RateLimitCommandEntity commandEntity = new RateLimitCommandEntity("gateway_001", "RS590LKPOD8877DDLMFKS4");

        boolean rateLimit = authRateLimitService.rateLimit(commandEntity);
        log.info("限流结果(第一次) rateLimit: {}", rateLimit);
        assertFalse(rateLimit); // false = 未被限流
    }

    @Test
    public void test_rateLimit_emptyApiKey() {
        RateLimitCommandEntity commandEntity = new RateLimitCommandEntity("gateway_001", "");

        boolean rateLimit = authRateLimitService.rateLimit(commandEntity);
        log.info("空 apiKey 限流结果 rateLimit: {}", rateLimit);
        assertFalse(rateLimit);
    }

    @Test
    @Transactional
    @Rollback
    public void test_rateLimit_withHighFrequency() {
        // 注册一个极低限流 key（360次/小时 = 0.1次/秒，10秒才恢复一个令牌）
        RegisterCommandEntity registerEntity = RegisterCommandEntity.builder()
                .gatewayId("gateway_001")
                .rateLimit(360)
                .expireTime(LocalDateTime.now().plusHours(1))
                .build();
        String apiKey = authRegisterService.register(registerEntity);
        log.info("注册低限流 apiKey: {}", apiKey);

        RateLimitCommandEntity commandEntity = new RateLimitCommandEntity("gateway_001", apiKey);

        // 第一次调用，消耗令牌
        boolean first = authRateLimitService.rateLimit(commandEntity);
        assertFalse(first);

        // 立即第二次调用，令牌桶空了，应被限流
        boolean second = authRateLimitService.rateLimit(commandEntity);
        log.info("连续第二次调用限流结果 rateLimit: {}", second);
        assertTrue(second); // true = 被限流
    }

}
