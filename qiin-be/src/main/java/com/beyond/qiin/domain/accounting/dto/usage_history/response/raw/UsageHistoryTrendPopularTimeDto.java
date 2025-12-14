package com.beyond.qiin.domain.accounting.dto.usage_history.response.raw;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UsageHistoryTrendPopularTimeDto {
    private Long assetId;
    private String assetName;
    private Integer totalMinutes; // 예약 총 시간
}
