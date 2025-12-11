package com.beyond.qiin.infra.redis.accounting.settlement;

import java.math.BigDecimal;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@RedisHash("settlement_performance_month")
public class SettlementPerformanceMonthReadModel {

    @Id
    private String key; // settlementPerformance:base:2024:3:all or settlementPerformance:compare:2024:3:all

    private BigDecimal value; // 해당 월의 saving 값 1개만 저장
}
