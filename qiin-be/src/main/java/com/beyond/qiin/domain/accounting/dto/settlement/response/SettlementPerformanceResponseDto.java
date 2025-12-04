package com.beyond.qiin.domain.accounting.dto.settlement.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SettlementPerformanceResponseDto {

    private AssetInfo asset;
    private YearRangeInfo yearRange;
    private List<MonthlyPerformance> monthlyData;
    private PerformanceSummary summary;

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
    public static class MonthlyPerformance {
        private int month;
        private BigDecimal baseYearSaving; // 기준연도 손익 합계
        private BigDecimal compareYearSaving; // 비교연도 손익 합계
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class PerformanceSummary {
        private BigDecimal baseYearTotalSaving; // 기준연도 총 절감금액
        private BigDecimal compareYearCurrentSaving; // 비교연도 (지난달까지) 절감금액
        private BigDecimal accumulatedSaving; // 누적 절감금액
    }
}
