package com.beyond.qiin.infra.redis.accounting.usage_history;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@RedisHash("usage_trend")
public class UsageHistoryTrendReadModel {

    @Id
    private String key; // usageTrend:2024:3:id-10

    private double usageRate; // 딱 필요한 값 1개만 저장
}
