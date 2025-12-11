package com.beyond.qiin.infra.redis.accounting.usage_history;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("usage_trend_top")
public class UsageHistoryTrendTopReadModel {

    @Id
    private String key; // usageTrendTop:2023:count  또는 usageTrendTop:2023:time

    private String json; // Top3 데이터를 JSON 문자열로 저장 (List 형태 그대로)
}
