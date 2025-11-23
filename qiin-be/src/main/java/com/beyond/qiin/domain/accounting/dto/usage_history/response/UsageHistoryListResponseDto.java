package com.beyond.qiin.domain.accounting.dto.usage_history.response;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class UsageHistoryListResponseDto {

    private final Long usageHistoryId;
    private final String assetName;

    private final Instant reservationStartAt;
    private final Instant reservationEndAt;

    private final Integer reservationMinutes;
    private String reservationDurationText;

    private final Instant actualStartAt;
    private final Instant actualEndAt;

    private final Integer actualMinutes;
    private String actualDurationText;

    private final BigDecimal usageRatioRaw;
    private String usageRatio;

    public void changeReservationDurationText(String text) {
        this.reservationDurationText = text;
    }

    public void changeActualDurationText(String text) {
        this.actualDurationText = text;
    }

    public void changeUsageRatioText(String text) {
        this.usageRatio = text;
    }
}
