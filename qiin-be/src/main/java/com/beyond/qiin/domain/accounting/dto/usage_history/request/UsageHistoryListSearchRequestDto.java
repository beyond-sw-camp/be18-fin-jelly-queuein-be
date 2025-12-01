package com.beyond.qiin.domain.accounting.dto.usage_history.request;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UsageHistoryListSearchRequestDto {

    private LocalDate startDate;
    private LocalDate endDate;

    private String keyword;
}
