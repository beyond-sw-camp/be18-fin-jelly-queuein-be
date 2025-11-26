package com.beyond.qiin.domain.accounting.dto.usage_history.response;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UsageHistoryTrendRawDto {
    private Long assetId;
    private String assetName;
    private int assetCount;

    private Map<Integer, UsageAggregate> baseYearData;
    private Map<Integer, UsageAggregate> compareYearData;

    @Getter
    @Builder
    public static class UsageAggregate {
        private Integer actualUsage;
        private Integer reservedUsage;
    }
}
