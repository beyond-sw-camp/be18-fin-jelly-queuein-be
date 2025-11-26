package com.beyond.qiin.domain.accounting.dto.usage_history.request;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UsageHistoryListSearchRequestDto {

    private Instant startDate;
    private Instant endDate;

    private String keyword;
}
