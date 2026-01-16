package com.beyond.qiin.infra.redis.reservation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationRedisAdapter {

    private final RedisTemplate<String, Object> redisTemplate;

    // 캐시 삭제
    public void evictUserReservationCache(Long userId) {
        if (userId == null) return; // 실제

        String pattern = "user-reservations::user:" + userId + ":*";

        redisTemplate.execute((RedisCallback<Void>) connection -> {
            Cursor<byte[]> cursor = connection.scan(
                    ScanOptions.scanOptions().match(pattern).count(100).build());

            while (cursor.hasNext()) {
                byte[] key = cursor.next();
                connection.del(key);

                if (log.isDebugEnabled()) {
                    log.debug("Evicted cache key={}", new String(key, StandardCharsets.UTF_8));
                }
            }

            return null;
        });
    }

}
