package com.beyond.qiin.infra.redis.accounting.settlement;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SettlementPerformanceMonthRedisAdapter {

    private final SettlementPerformanceMonthRedisRepository repository;
    private final RedisTemplate<String, Object> redisTemplate;

    //     월별 절감금액 저장
    //
    //     @param key   Redis에 저장할 Key
    //     @param value 저장할 절감 금액
    //     @param hours TTL (null 이면 무기한)

    public void save(String key, java.math.BigDecimal value, Long hours) {

        // 1) 레코드 저장 (무기한 저장)
        SettlementPerformanceMonthReadModel model = SettlementPerformanceMonthReadModel.builder()
                .key(key)
                .value(value)
                .build();

        repository.save(model);

        // 2) TTL 설정 (optional)
        if (hours != null && hours >= 1) {
            redisTemplate.expire(key, Duration.ofHours(hours));
        }
    }

    // Redis에서 월별 절감금액 조회

    public java.math.BigDecimal get(String key) {
        return repository
                .findById(key)
                .map(SettlementPerformanceMonthReadModel::getValue)
                .orElse(null);
    }

    // Redis 값 삭제

    public void delete(String key) {
        repository.deleteById(key);
    }
}
