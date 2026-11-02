package com.example.demo.service;

import java.time.Duration;
import java.util.Map;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisDemoService {
    private final StringRedisTemplate redis;

    public RedisDemoService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public String set(String key, String value, long ttlSeconds) {
        if (ttlSeconds > 0) redis.opsForValue().set(key, value, Duration.ofSeconds(ttlSeconds));
        else redis.opsForValue().set(key, value);
        return redis.opsForValue().get(key);
    }
    public String get(String key) { return redis.opsForValue().get(key); }
    public Boolean delete(String key) { return redis.delete(key); }
    public Boolean exists(String key) { return Boolean.TRUE.equals(redis.hasKey(key)); }
    public Long increment(String key) { return redis.opsForValue().increment(key); }
    public Boolean expire(String key, long seconds) { return redis.expire(key, Duration.ofSeconds(seconds)); }
    public Long ttl(String key) { return redis.getExpire(key); }
    public Long listPush(String key, String value) { return redis.opsForList().rightPush(key, value); }
    public Map<Object, Object> hashPut(String key, String field, String value) {
        redis.opsForHash().put(key, field, value);
        return redis.opsForHash().entries(key);
    }
}
