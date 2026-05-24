package cn.laterya.ai.domain.auth;

import cn.laterya.ai.domain.auth.model.entity.RegisterCommandEntity;
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
@Transactional
@Rollback
@SpringBootTest
@ActiveProfiles("test")
public class AuthRegisterServiceTest {

    @Resource
    private IAuthRegisterService authRegisterService;

    @Test
    public void test_register() {
        RegisterCommandEntity commandEntity = RegisterCommandEntity.builder()
                .gatewayId("gateway_001")
                .rateLimit(36000)
                .expireTime(LocalDateTime.now().plusDays(2))
                .build();

        String apiKey = authRegisterService.register(commandEntity);
        log.info("注册结果 apiKey: {}", apiKey);

        assertNotNull(apiKey);
        assertTrue(apiKey.startsWith("gw-"));
        assertTrue(apiKey.length() > 48);
    }

}
