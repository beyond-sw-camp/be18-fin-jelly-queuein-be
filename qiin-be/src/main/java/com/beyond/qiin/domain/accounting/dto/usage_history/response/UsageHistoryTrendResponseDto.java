package com.beyond.qiin.domain.accounting.dto.usage_history.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UsageHistoryTrendResponseDto {

    private AssetInfo asset; // ← 이거 한 줄 추가!
    private YearRangeInfo yearRange;
    private List<MonthlyUsageData> monthlyData;
    private UsageIncreaseSummary summary;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class AssetInfo {
        private Long assetId;
        private String assetName;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class YearRangeInfo {
        private int baseYear;
        private int compareYear;
        private int months;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class MonthlyUsageData {
        private int month;
        private Double baseYearUsageRate;
        private Double compareYearUsageRate;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class UsageIncreaseSummary {
        private Double usageRateIncrease;
        private Double actualUsageIncrease;
        private Double resourceUtilizationIncrease;
    }
}
