package com.beyond.qiin.infra.redis.iam.permission;

import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PermissionCacheAdapter {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String KEY_PREFIX = "USER_PERMISSIONS:";

    public void save(final Long userId, final List<String> permissions, final Duration ttl) {
        redisTemplate.opsForValue().set(KEY_PREFIX + userId, permissions, ttl);
    }

    @SuppressWarnings("unchecked")
    public List<String> get(final Long userId) {
        return (List<String>) redisTemplate.opsForValue().get(KEY_PREFIX + userId);
    }

    public void evict(final Long userId) {
        redisTemplate.delete(KEY_PREFIX + userId);
    }
}
