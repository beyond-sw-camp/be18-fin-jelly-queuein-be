package com.beyond.qiin.domain.booking.queue;

import static com.beyond.qiin.domain.booking.queue.WaitingQueueConstants.ACTIVE_KEY;
import static com.beyond.qiin.domain.booking.queue.WaitingQueueConstants.REDIS_NAMESPACE;
import static com.beyond.qiin.domain.booking.queue.WaitingQueueConstants.WAIT_KEY;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
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
        redisTemplate.opsForZSet().removeRange(REDIS_NAMESPACE + key, start, end);
    }

    // set 자료구조에 값 추가 - 활성 사용자의 순서는 x
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

    // set 안에 특정 값(member) 있는지 확인
    public Boolean setIsMember(String key, String value) {
        return redisTemplate.opsForSet().isMember(REDIS_NAMESPACE + key, value);
    }

    // key에 ttl 설정(timeout : 숫자값, unit : 시간 단위) -> 일시적 데이터
    public void setTtl(String key, long timeout, TimeUnit unit) {
        redisTemplate.expire(REDIS_NAMESPACE + key, timeout, unit);
    }

    // set의 해당 구간 사용자들을 가져오는 용도
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

    public long countActiveTokens() {
        long count = 0;

        var scanOptions = ScanOptions.scanOptions()
            .match(REDIS_NAMESPACE + ACTIVE_KEY + ":*")
            .count(100)
            .build();

        var connection = redisTemplate.getConnectionFactory().getConnection();

        try (Cursor<byte[]> cursor = connection.scan(scanOptions)) {
            while (cursor.hasNext()) {
                cursor.next(); // key 값은 중요하지 않음
                count++;
            }
        }

        return count;
    }


    public void deleteKey(String key) {
        redisTemplate.delete(REDIS_NAMESPACE + key);
    }

    // 현재 활성 토큰에 대한 계산
//    public Long countActiveTokens() {
//        // 레디스 서버에서 원자적 계산 (race condition 차단)
//        String luaScript =
//            "local keys = redis.call('keys', ARGV[1]) \n" +
//                "return table.getn(keys)";
//        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
//        redisScript.setScriptText(luaScript);
//        redisScript.setResultType(Long.class);
//        return redisTemplate.execute(redisScript, Collections.emptyList(), ACTIVE_COUNT_KEY);
//    }
//    public Long countActiveTokensWithoutLua() {
//        return redisTemplate.opsForSet()
//            .size(REDIS_NAMESPACE + ACTIVE_KEY); //등록한 redis key
//    }
}
