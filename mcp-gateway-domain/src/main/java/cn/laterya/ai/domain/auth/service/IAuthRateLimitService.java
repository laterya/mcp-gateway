package cn.laterya.ai.domain.auth.service;

import cn.laterya.ai.domain.auth.model.entity.RateLimitCommandEntity;

public interface IAuthRateLimitService {

    /**
     * 限流操作
     * true - 限流
     * false - 未限流
     */
    boolean rateLimit(RateLimitCommandEntity commandEntity);

}
