package cn.laterya.ai.domain.auth;

import cn.laterya.ai.domain.auth.model.entity.LicenseCommandEntity;
import cn.laterya.ai.domain.auth.service.IAuthLicenseService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class AuthLicenseServiceTest {

    @Resource
    private IAuthLicenseService authLicenseService;

    @Test
    public void test_checkLicense_withSeedData() {
        // 种子数据：gateway_001 + RS590LKPOD8877DDLMFKS4，auth=1（强校验），expire=2029
        LicenseCommandEntity commandEntity = new LicenseCommandEntity("gateway_001", "RS590LKPOD8877DDLMFKS4");

        boolean success = authLicenseService.checkLicense(commandEntity);
        log.info("鉴权结果 success: {}", success);
        assertTrue(success);
    }

    @Test
    public void test_checkLicense_invalidApiKey() {
        LicenseCommandEntity commandEntity = new LicenseCommandEntity("gateway_001", "INVALID_KEY");

        boolean success = authLicenseService.checkLicense(commandEntity);
        log.info("无效 apiKey 鉴权结果 success: {}", success);
        assertFalse(success);
    }

}
