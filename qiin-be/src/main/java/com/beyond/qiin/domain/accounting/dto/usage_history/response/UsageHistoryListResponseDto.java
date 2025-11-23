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
    private final String reservationDurationText;

    private final Instant actualStartAt;
    private final Instant actualEndAt;

    private final Integer actualMinutes;
    private final String actualDurationText;

    private final BigDecimal usageRatioRaw;
    private final String usageRatio;

    /**
     * 기존 DTO(raw) 값에서 텍스트 변환 값을 적용한 새로운 DTO 생성
     */
    public UsageHistoryListResponseDto withConvertedValues(
            String reservationDurationText, String actualDurationText, String usageRatioText) {
        return new UsageHistoryListResponseDto(
                this.usageHistoryId,
                this.assetName,
                this.reservationStartAt,
                this.reservationEndAt,
                this.reservationMinutes,
                reservationDurationText,
                this.actualStartAt,
                this.actualEndAt,
                this.actualMinutes,
                actualDurationText,
                this.usageRatioRaw,
                usageRatioText);
    }
}
