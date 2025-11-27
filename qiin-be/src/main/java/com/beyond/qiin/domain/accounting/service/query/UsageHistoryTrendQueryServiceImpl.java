// file: src/main/java/com/beyond/qiin/domain/accounting/service/query/UsageHistoryTrendQueryServiceImpl.java
package com.beyond.qiin.domain.accounting.service.query;

import com.beyond.qiin.domain.accounting.dto.common.ReportingComparisonRequestDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryTrendResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryTrendResponseDto.*;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.raw.UsageHistoryTrendRawDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.raw.UsageHistoryTrendRawDto.UsageAggregate;
import com.beyond.qiin.domain.accounting.repository.querydsl.UsageHistoryTrendQueryAdapter;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsageHistoryTrendQueryServiceImpl implements UsageHistoryTrendQueryService {

    private final UsageHistoryTrendQueryAdapter trendQueryAdapter;

    @Override
    public UsageHistoryTrendResponseDto getUsageHistoryTrend(ReportingComparisonRequestDto request) {

        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();

        int baseYear = request.getBaseYear() != null ? request.getBaseYear() : currentYear - 1;
        int compareYear = request.getCompareYear() != null ? request.getCompareYear() : currentYear;

        UsageHistoryTrendRawDto raw =
                trendQueryAdapter.getTrendData(baseYear, compareYear, request.getAssetId(), request.getAssetName(), 0);

        // 월별 데이터 1~12월
        List<MonthlyUsageData> monthlyList = buildMonthlyList(raw.getBaseYearData(), raw.getCompareYearData());

        // summary 계산: valid month 기준
        UsageIncreaseSummary summary =
                calculateIncrease(raw.getBaseYearData(), raw.getCompareYearData(), raw.getAssetCount(), currentMonth);

        return UsageHistoryTrendResponseDto.builder()
                .asset(new AssetInfo(raw.getAssetId(), raw.getAssetName()))
                .yearRange(new YearRangeInfo(baseYear, compareYear, 12))
                .monthlyData(monthlyList)
                .summary(summary)
                .build();
    }

    // -------------------------------------------------------------------------
    // 1~12월 전체 반환 (UI 표시용)
    // -------------------------------------------------------------------------
    private List<MonthlyUsageData> buildMonthlyList(
            Map<Integer, UsageAggregate> baseData, Map<Integer, UsageAggregate> compareData) {
        List<MonthlyUsageData> result = new ArrayList<>();

        for (int m = 1; m <= 12; m++) {
            UsageAggregate b = baseData.getOrDefault(m, empty());
            UsageAggregate c = compareData.getOrDefault(m, empty());

            result.add(MonthlyUsageData.builder()
                    .month(m)
                    .baseYearUsageRate(round1(calcUsageRate(b)))
                    .compareYearUsageRate(round1(calcUsageRate(c)))
                    .build());
        }
        return result;
    }

    private UsageAggregate empty() {
        return UsageAggregate.builder().actualUsage(0).reservedUsage(0).build();
    }

    private double calcUsageRate(UsageAggregate agg) {
        if (agg.getReservedUsage() == 0) return 0.0;
        return (double) agg.getActualUsage() / agg.getReservedUsage() * 100.0;
    }

    // -------------------------------------------------------------------------
    // summary 계산: 월별로 valid month만 비교
    // -------------------------------------------------------------------------
    private UsageIncreaseSummary calculateIncrease(
            Map<Integer, UsageAggregate> base, Map<Integer, UsageAggregate> compare, int assetCount, int currentMonth) {

        // ✔ 월별로 base와 compare 둘 다 reservedUsage>0인 월만 선택
        List<Integer> validMonths = IntStream.rangeClosed(1, 12)
                .filter(m -> m < currentMonth) // 현재 월 제외
                .filter(m -> base.get(m).getReservedUsage() > 0)
                .filter(m -> compare.get(m).getReservedUsage() > 0)
                .boxed()
                .collect(Collectors.toList());

        // ✔ 유효 월 없음 → summary = 0
        if (validMonths.isEmpty()) {
            return UsageIncreaseSummary.builder()
                    .usageRateIncrease(0.0)
                    .actualUsageIncrease(0.0)
                    .resourceUtilizationIncrease(0.0)
                    .build();
        }

        // 합산
        int baseActual =
                validMonths.stream().mapToInt(m -> base.get(m).getActualUsage()).sum();
        int compareActual = validMonths.stream()
                .mapToInt(m -> compare.get(m).getActualUsage())
                .sum();

        int baseReserved = validMonths.stream()
                .mapToInt(m -> base.get(m).getReservedUsage())
                .sum();
        int compareReserved = validMonths.stream()
                .mapToInt(m -> compare.get(m).getReservedUsage())
                .sum();

        // ✔ 비교년도 데이터가 없어도 summary=0
        if (baseReserved == 0 || compareReserved == 0) {
            return UsageIncreaseSummary.builder()
                    .usageRateIncrease(0.0)
                    .actualUsageIncrease(0.0)
                    .resourceUtilizationIncrease(0.0)
                    .build();
        }

        // 증가율 계산
        double usageRateIncrease = ((double) (compareReserved - baseReserved) / baseReserved) * 100.0;

        double baseRatio = (double) baseActual / baseReserved;
        double compareRatio = (double) compareActual / compareReserved;
        double actualUsageIncrease = (compareRatio - baseRatio) * 100.0;

        int months = validMonths.size();
        int totalMinutes = assetCount * 24 * 60 * months;

        double baseUtil = (double) baseReserved / totalMinutes;
        double compareUtil = (double) compareReserved / totalMinutes;
        double utilizationIncrease = (compareUtil - baseUtil) * 100.0;

        return UsageIncreaseSummary.builder()
                .usageRateIncrease(round1(usageRateIncrease))
                .actualUsageIncrease(round1(actualUsageIncrease))
                .resourceUtilizationIncrease(round1(utilizationIncrease))
                .build();
    }

    private double round1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
