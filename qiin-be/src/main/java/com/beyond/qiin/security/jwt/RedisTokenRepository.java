package com.beyond.qiin.security.jwt;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisTokenRepository {

    private final StringRedisTemplate redis;

    private static final String REFRESH_PREFIX = "REFRESH_TOKEN:";
    private static final String BLACKLIST_PREFIX = "BLACKLIST:";

    // Refresh Token 저장
    public void saveRefreshToken(final Long userId, final String refreshToken, final Duration ttl) {
        redis.opsForValue().set(REFRESH_PREFIX + userId, refreshToken, ttl);
    }

    // Refresh Token 조회
    public String getRefreshToken(final Long userId) {
        return redis.opsForValue().get(REFRESH_PREFIX + userId);
    }

    // Refresh Token 삭제
    public void deleteRefreshToken(final Long userId) {
        redis.delete(REFRESH_PREFIX + userId);
    }

    // AccessToken 블랙리스트 저장
    public void blacklistAccessToken(final String accessToken, final Duration ttl) {
        redis.opsForValue().set(BLACKLIST_PREFIX + accessToken, "logout", ttl);
    }

    // 블랙리스트 여부 검사
    public boolean isBlacklisted(final String accessToken) {
        return redis.hasKey(BLACKLIST_PREFIX + accessToken);
    }
}
