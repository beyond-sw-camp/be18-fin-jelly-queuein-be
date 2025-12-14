package com.beyond.qiin.infra.redis.accounting.settlement;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SettlementPerformanceMonthRedisAdapter {

    private final SettlementPerformanceMonthRedisRepository repository;

    // 월별 / 연도별 절감금액 저장 (영구 캐시)
    public void save(String key, BigDecimal value) {
        repository.save(SettlementPerformanceMonthReadModel.builder()
                .key(key)
                .value(value)
                .build());
    }

    public BigDecimal get(String key) {
        return repository
                .findById(key)
                .map(SettlementPerformanceMonthReadModel::getValue)
                .orElse(null);
    }

    public void delete(String key) {
        repository.deleteById(key);
    }
}
