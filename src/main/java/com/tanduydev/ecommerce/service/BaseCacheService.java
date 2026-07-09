package com.tanduydev.ecommerce.service;

import java.util.concurrent.TimeUnit;

public interface BaseCacheService {
    <T> T get(String key, Class<T> clazz);

    void put(String key, Object value, long ttl, TimeUnit unit);

    void evict(String key);
    void evictByPattern(String pattern);
}
