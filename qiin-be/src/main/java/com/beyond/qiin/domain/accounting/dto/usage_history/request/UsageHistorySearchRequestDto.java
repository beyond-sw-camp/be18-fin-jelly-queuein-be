package com.beyond.qiin.domain.accounting.dto.usage_history.request;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UsageHistorySearchRequestDto {

    private Instant startDate;
    private Instant endDate;

    private String keyword;
}
