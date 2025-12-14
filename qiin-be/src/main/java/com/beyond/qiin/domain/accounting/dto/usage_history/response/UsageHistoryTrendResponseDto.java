package com.beyond.qiin.domain.accounting.dto.usage_history.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UsageHistoryTrendResponseDto {

    private AssetInfo asset;
    private YearRangeInfo yearRange;

    private List<MonthlyUsageData> monthlyData;
    private Double actualUsageIncrease;

    private PopularGroup<PopularCountDto> popularByCount;
    private PopularGroup<PopularTimeDto> popularByTime;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class PopularGroup<T> {
        private List<T> baseYear;
        private List<T> compareYear;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class PopularCountDto {
        private Long assetId;
        private String assetName;
        private Integer count;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class PopularTimeDto {
        private Long assetId;
        private String assetName;
        private Integer totalMinutes;
    }

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
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class MonthlyUsageData {
        private int month;
        private Double baseYearUsageRate;
        private Double compareYearUsageRate;
    }
}
