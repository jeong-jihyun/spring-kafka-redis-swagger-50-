package com.example.demo.service;

import java.time.Duration;
import java.util.Map;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisDemoService {
    // 문자열 기반 Redis 명령을 실행하는 Spring Data Redis 템플릿이다.
    private final StringRedisTemplate redis;

    public RedisDemoService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    /**
     * ttlSeconds가 양수이면 만료 시간을 함께 설정한다.
     * 0이면 만료 시간을 지정하지 않는다. 음수 입력은 Controller의 @Min 검증에서 거부한다.
     */
    public String set(String key, String value, long ttlSeconds) {
        if (ttlSeconds > 0) redis.opsForValue().set(key, value, Duration.ofSeconds(ttlSeconds));
        else redis.opsForValue().set(key, value);
        return redis.opsForValue().get(key);
    }

    /** key에 해당하는 문자열을 읽는다. 키가 없으면 null이다. */
    public String get(String key) { return redis.opsForValue().get(key); }

    /** key를 삭제하고 실제 삭제 여부를 반환한다. */
    public Boolean delete(String key) { return redis.delete(key); }

    /** 존재 여부 조회 결과가 null일 수 있으므로 Boolean.TRUE로 정규화한다. */
    public Boolean exists(String key) { return Boolean.TRUE.equals(redis.hasKey(key)); }

    /** Redis INCR 명령을 사용하므로 서버에서 원자적으로 증가한다. */
    public Long increment(String key) { return redis.opsForValue().increment(key); }

    /** 기존 key의 만료 시간을 갱신한다. */
    public Boolean expire(String key, long seconds) { return redis.expire(key, Duration.ofSeconds(seconds)); }

    /** 남은 TTL을 초 단위로 조회한다. Redis의 특수 반환값도 호출부에서 고려해야 한다. */
    public Long ttl(String key) { return redis.getExpire(key); }

    /** Redis List의 오른쪽에 새 값을 추가한다. */
    public Long listPush(String key, String value) { return redis.opsForList().rightPush(key, value); }

    /** Hash field를 저장한 뒤 현재 hash 전체를 반환한다. */
    public Map<Object, Object> hashPut(String key, String field, String value) {
        redis.opsForHash().put(key, field, value);
        return redis.opsForHash().entries(key);
    }
}
