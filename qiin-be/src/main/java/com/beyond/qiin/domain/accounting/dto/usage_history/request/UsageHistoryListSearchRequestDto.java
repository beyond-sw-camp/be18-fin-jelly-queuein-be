package com.beyond.qiin.domain.accounting.dto.usage_history.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class UsageHistoryListSearchRequestDto {

    private LocalDate startDate;
    private LocalDate endDate;

    private String keyword;
}
