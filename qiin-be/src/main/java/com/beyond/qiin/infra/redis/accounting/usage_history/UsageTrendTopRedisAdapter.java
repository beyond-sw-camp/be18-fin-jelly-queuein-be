package com.beyond.qiin.infra.redis.accounting.usage_history;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsageTrendTopRedisAdapter {

    private final UsageHistoryTrendTopRedisRepository repository;
    private final RedisTemplate<String, Object> redisTemplate;

    public void save(String key, String json, Long hours) {

        UsageHistoryTrendTopReadModel model =
                UsageHistoryTrendTopReadModel.builder().key(key).json(json).build();

        repository.save(model);

        if (hours != null && hours >= 1) {
            redisTemplate.expire(key, Duration.ofHours(hours));
        }
    }

    public String get(String key) {
        return repository
                .findById(key)
                .map(UsageHistoryTrendTopReadModel::getJson)
                .orElse(null);
    }
}
