package com.beyond.qiin.infra.redis.accounting.usage_history;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsageTrendRedisAdapter {

    private final UsageHistoryTrendRedisRepository repository;
    private final RedisTemplate<String, Object> redisTemplate;

    public void save(String key, double value, Long hours) {

        // 1) 레코드 저장 (무기한)
        UsageHistoryTrendReadModel model =
                UsageHistoryTrendReadModel.builder().key(key).usageRate(value).build();

        repository.save(model);

        // 2) TTL 설정 (Repository 대신 RedisTemplate로)
        if (hours != null && hours >= 1) {
            redisTemplate.expire(key, Duration.ofHours(hours));
        }
    }

    public Double get(String key) {
        return repository
                .findById(key)
                .map(UsageHistoryTrendReadModel::getUsageRate)
                .orElse(null);
    }

    public void delete(String key) {
        repository.deleteById(key);
    }
}
