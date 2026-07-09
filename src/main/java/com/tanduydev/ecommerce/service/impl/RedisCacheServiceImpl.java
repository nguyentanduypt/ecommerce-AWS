package com.tanduydev.ecommerce.service.impl;

import com.tanduydev.ecommerce.service.BaseCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCacheServiceImpl implements BaseCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public <T> T get(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null && clazz.isInstance(value)) {
                return clazz.cast(value); // Ép kiểu an toàn
            }
            return null;
        } catch (Exception e) {
            // Nếu Redis lỗi (chết server, timeout...), log lại và trả về null.
            // Service gọi nó sẽ thấy null và tự động Fallback xuống query Database.
            log.error("[REDIS] GET cache failed for key: {} - Error: {}", key, e.getMessage());
            return null;
        }
    }

    @Override
    public void put(String key, Object value, long ttl, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, ttl, unit);
        } catch (Exception e) {
            log.error("[REDIS] PUT cache failed for key: {} - Error: {}", key, e.getMessage());
        }
    }

    @Override
    public void evict(String key) {
        try {
            redisTemplate.delete(key);
            log.info("[REDIS] EVICTED cache for key: {}", key);
        } catch (Exception e) {
            log.error("[REDIS] EVICT cache failed for key: {} - Error: {}", key, e.getMessage());
        }
    }

    @Override
    public void evictByPattern(String pattern) {
        try {
            var keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("[REDIS] EVICTED {} keys matching pattern: {}", keys.size(), pattern);
            }
        } catch (Exception e) {
            log.error("[REDIS] EVICT PATTERN failed for pattern: {} - Error: {}", pattern, e.getMessage());
        }
    }
}