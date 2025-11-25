package com.beyond.qiin.domain.accounting.dto.usage_history.response;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UsageHistoryListResponseDto {

    private final Long usageHistoryId;
    private final String assetName;

    private final Instant reservationStartAt;
    private final Instant reservationEndAt;

    private final Integer reservationMinutes; // INT
    private final Instant actualStartAt;
    private final Instant actualEndAt;

    private final Integer actualMinutes; // INT
    private final BigDecimal usageRatio; // DECIMAL(12,3)
}
