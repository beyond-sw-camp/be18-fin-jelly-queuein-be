package com.beyond.qiin.domain.accounting.dto.usage_history.response;

import com.beyond.qiin.domain.accounting.entity.UsageHistory;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UsageHistoryResponse {

    private final Long usageHistoryId;

    private final String assetName;

    private final Instant reservationStartAt;
    private final Instant reservationEndAt;
    private final String reservationDurationText;

    private final Instant actualStartAt;
    private final Instant actualEndAt;
    private final String actualDurationText;

    private final BigDecimal usageRatio;

    // 엔티티 + 조인된 assetName을 함께 받는 방식
    public static UsageHistoryResponse from(
            UsageHistory u, String assetName, String reservationDurationText, String actualDurationText) {
        return UsageHistoryResponse.builder()
                .usageHistoryId(u.getId())
                .assetName(assetName)
                .reservationStartAt(u.getStartAt())
                .reservationEndAt(u.getEndAt())
                .reservationDurationText(reservationDurationText)
                .actualStartAt(u.getActualStartAt())
                .actualEndAt(u.getActualEndAt())
                .actualDurationText(actualDurationText)
                .usageRatio(u.getUsageRatio())
                .build();
    }
}
