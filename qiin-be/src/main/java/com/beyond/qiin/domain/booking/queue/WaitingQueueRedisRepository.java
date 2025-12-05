package com.beyond.qiin.domain.booking.queue;

import static com.beyond.qiin.domain.booking.queue.WaitingQueueConstants.ACTIVE_COUNT_KEY;
import static com.beyond.qiin.domain.booking.queue.WaitingQueueConstants.REDIS_NAMESPACE;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class WaitingQueueRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    // sorted set 추가 - score : 토큰 순서 결정
    public Boolean zSetAdd(String key, String value, double score) {
        return redisTemplate.opsForZSet().addIfAbsent(REDIS_NAMESPACE + key, value, score);
    }

    // sorted set 삭제
    public void zSetRemove(String key, String value) {
        redisTemplate.opsForZSet().remove(REDIS_NAMESPACE + key, value);
    }

    // sorted set 현재 순서 구하기 (rank)
    public Long zSetRank(String key, String token) {
        return redisTemplate.opsForZSet().rank(REDIS_NAMESPACE + key, token);
    }

    // batch로 활성화해 삭제 가능
    public void zSetRemoveRange(String key, int start, int end) {
        redisTemplate.opsForZSet().remove(REDIS_NAMESPACE + key, start, end);
    }

    // set 자료구조에 값 추가
    public Long setAdd(String key, String value) {
        return redisTemplate.opsForSet().add(REDIS_NAMESPACE + key, value);
    }

    // 여러 토큰을 set에 추가
    public void setAddRangeWithTtl(String key, Set<String> value, long timeout, TimeUnit unit) {
        value.forEach(token -> {
            String[] tokenInfo = token.split(":");
            redisTemplate.opsForSet().add(REDIS_NAMESPACE + key + ":" + tokenInfo[0], tokenInfo[1]);
            setTtl(key + ":" + tokenInfo[0], timeout, unit);
        });
    }

    // set 안에 특정 값 있는지 확인
    public Boolean setIsMember(String key, String value) {
        return redisTemplate.opsForSet().isMember(REDIS_NAMESPACE + key, value);
    }

    // key에 ttl 설정(timeout : 숫자값, unit : 시간 단위)
    public void setTtl(String key, long timeout, TimeUnit unit) {
        redisTemplate.expire(REDIS_NAMESPACE + key, timeout, unit);
    }

    public Set<String> zSetGetRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(REDIS_NAMESPACE + key, start, end);
    }

    public void clearCurrentDB() {
        RedisConnection connection = null;
        try {
            connection = RedisConnectionUtils.getConnection(redisTemplate.getConnectionFactory());
            connection.serverCommands().flushDb();
        } finally {
            if (connection != null) {
                RedisConnectionUtils.releaseConnection(connection, redisTemplate.getConnectionFactory());
            }
        }
    }

    // 현재 활성 토큰에 대한 계산
    public Long countActiveTokens() {
        // 레디스 서버에서 원자적 계산 (race condition 차단)
        String luaScript = "local count = 0" + "for _, key in ipairs(redis.call('keys', ARGV[1])) do"
                + "count = count + 1"
                + "end"
                + "return count";
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(luaScript);
        redisScript.setResultType(Long.class);
        return redisTemplate.execute(redisScript, Collections.emptyList(), ACTIVE_COUNT_KEY);
    }

    public void deleteKey(String key) {
        redisTemplate.delete(REDIS_NAMESPACE + key);
    }
}
