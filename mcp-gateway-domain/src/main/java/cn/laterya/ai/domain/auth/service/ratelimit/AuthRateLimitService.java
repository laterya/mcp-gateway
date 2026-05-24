package cn.laterya.ai.domain.auth.service.ratelimit;

import cn.laterya.ai.domain.auth.adapter.repository.IAuthRepository;
import cn.laterya.ai.domain.auth.model.entity.LicenseCommandEntity;
import cn.laterya.ai.domain.auth.model.entity.RateLimitCommandEntity;
import cn.laterya.ai.domain.auth.model.valobj.McpGatewayAuthVO;
import cn.laterya.ai.domain.auth.service.IAuthRateLimitService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.RateLimiter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 调用限流服务
 *
 * 使用 Guava RateLimiter 做令牌桶限流，按 gatewayId+apiKey 维度隔离。
 * RateLimiter 实例缓存在 Guava Cache 中，1 小时未访问自动清除，避免内存泄漏。
 */
@Slf4j
@Service
public class AuthRateLimitService implements IAuthRateLimitService {

    @Resource
    private IAuthRepository repository;

    private final Cache<String, RateLimiter> rateLimiterCache = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .build();

    @Override
    public boolean rateLimit(RateLimitCommandEntity commandEntity) {
        String gatewayId = commandEntity.getGatewayId();
        String apiKey = commandEntity.getApiKey();

        if (null == apiKey || apiKey.isEmpty()) return false;

        try {
            // 首次访问时从 DB 加载限流配置并创建 RateLimiter，后续直接复用
            RateLimiter rateLimiter = rateLimiterCache.get(gatewayId + "_" + apiKey, () -> {
                McpGatewayAuthVO mcpGatewayAuthVO = repository.queryEffectiveGatewayAuthInfo(new LicenseCommandEntity(gatewayId, apiKey));
                if (null == mcpGatewayAuthVO || null == mcpGatewayAuthVO.getRateLimit()) {
                    throw new IllegalStateException("未配置限流");
                }

                // 数据库存的是次/小时，RateLimiter 需要次/秒
                double permitsPerSecond = (double) mcpGatewayAuthVO.getRateLimit() / 3600;
                if (permitsPerSecond <= 0) {
                    throw new IllegalArgumentException("限流值不正确");
                }

                return RateLimiter.create(permitsPerSecond);
            });

            // tryAcquire：获取到令牌返回 true（未限流），取反后 true 表示被限流
            return !rateLimiter.tryAcquire();

        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            // 无配置 → 不限流（放行）
            if (cause instanceof IllegalStateException) {
                return false;
            }
            // 配置值 ≤ 0 → 限流（禁止访问）
            if (cause instanceof IllegalArgumentException) {
                return true;
            }
            log.error("限流校验失败 gatewayId:{} apiKey:{}", gatewayId, apiKey, e);
            return false;
        }
    }

}
