package com.beyond.qiin.domain.accounting.dto.usage_history.response.raw;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UsageHistoryTrendPopularCountDto {
    private Long assetId;
    private String assetName;
    private Integer count; // 예약 건수
}
