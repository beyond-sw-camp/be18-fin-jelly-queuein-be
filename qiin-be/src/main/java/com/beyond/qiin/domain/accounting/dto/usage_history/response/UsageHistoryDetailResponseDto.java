package com.beyond.qiin.domain.accounting.dto.usage_history.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UsageHistoryDetailResponseDto {

    private final Long usageHistoryId;

    private final String assetName;
    private final String assetImage; // S3 이미지라 null 들어감

    private final List<String> reserverNames;

    private final BigDecimal billAmount;
    private final BigDecimal actualBillAmount;
}
