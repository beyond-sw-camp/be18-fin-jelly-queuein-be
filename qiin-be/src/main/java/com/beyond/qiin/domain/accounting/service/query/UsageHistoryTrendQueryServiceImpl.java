package com.beyond.qiin.domain.accounting.service.query;

import com.beyond.qiin.domain.accounting.dto.common.request.ReportingComparisonRequestDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryTrendResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryTrendResponseDto.AssetInfo;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryTrendResponseDto.MonthlyUsageData;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryTrendResponseDto.UsageIncreaseSummary;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryTrendResponseDto.YearRangeInfo;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.raw.UsageHistoryTrendRawDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.raw.UsageHistoryTrendRawDto.UsageAggregate;
import com.beyond.qiin.domain.accounting.repository.querydsl.UsageHistoryTrendQueryAdapter;
import com.beyond.qiin.infra.redis.accounting.usage_history.UsageTrendRedisAdapter;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsageHistoryTrendQueryServiceImpl implements UsageHistoryTrendQueryService {

    private final UsageHistoryTrendQueryAdapter trendQueryAdapter;
    private final UsageTrendRedisAdapter redisAdapter;

    @Override
    public UsageHistoryTrendResponseDto getUsageHistoryTrend(ReportingComparisonRequestDto request) {

        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();

        int baseYear = request.getBaseYear() != null ? request.getBaseYear() : currentYear - 1;
        int compareYear = request.getCompareYear() != null ? request.getCompareYear() : currentYear;

        Long assetId = request.getAssetId();
        String assetName = request.getAssetName();

        UsageHistoryTrendRawDto raw = trendQueryAdapter.getTrendData(baseYear, compareYear, assetId, assetName, 0);

        Map<Integer, UsageAggregate> baseRaw = Optional.ofNullable(raw)
                .map(UsageHistoryTrendRawDto::getBaseYearData)
                .orElse(Collections.emptyMap());

        Map<Integer, UsageAggregate> compareRaw = Optional.ofNullable(raw)
                .map(UsageHistoryTrendRawDto::getCompareYearData)
                .orElse(Collections.emptyMap());

        // 중복 제거한 공통 메서드 사용
        Map<Integer, UsageAggregate> baseData =
                loadYearlyDataWithCache(baseYear, currentYear, currentMonth, assetId, assetName, baseRaw);

        Map<Integer, UsageAggregate> compareData =
                loadYearlyDataWithCache(compareYear, currentYear, currentMonth, assetId, assetName, compareRaw);

        List<MonthlyUsageData> monthlyList = buildMonthlyList(baseData, compareData);

        int assetCount = Optional.ofNullable(raw)
                .map(UsageHistoryTrendRawDto::getAssetCount)
                .orElse(0);

        UsageIncreaseSummary summary = calculateIncrease(baseData, compareData, assetCount, currentMonth);

        Long finalAssetId = Optional.ofNullable(raw)
                .map(UsageHistoryTrendRawDto::getAssetId)
                .orElse(null);

        String finalAssetName = Optional.ofNullable(raw)
                .map(UsageHistoryTrendRawDto::getAssetName)
                .orElse(null);

        return UsageHistoryTrendResponseDto.builder()
                .asset(new AssetInfo(finalAssetId, finalAssetName))
                .yearRange(new YearRangeInfo(baseYear, compareYear, 12))
                .monthlyData(monthlyList)
                .summary(summary)
                .build();
    }

    // 월별 캐싱 처리 (baseYear, compareYear 공통 처리)
    private Map<Integer, UsageAggregate> loadYearlyDataWithCache(
            int targetYear,
            int currentYear,
            int currentMonth,
            Long assetId,
            String assetName,
            Map<Integer, UsageAggregate> rawData) {
        Map<Integer, UsageAggregate> result = new HashMap<>();

        for (int month = 1; month <= 12; month++) {

            boolean isCurrentMonth = (targetYear == currentYear && month == currentMonth);
            String key = buildCacheKey(targetYear, month, assetId, assetName);

            if (!isCurrentMonth) {
                Object cached = redisAdapter.get(key);
                if (cached instanceof UsageAggregate agg) {
                    result.put(month, agg);
                    continue;
                }
            }

            UsageAggregate agg = rawData.getOrDefault(month, empty());
            result.put(month, agg);

            if (!isCurrentMonth) {
                redisAdapter.save(key, agg, 0);
            }
        }

        return result;
    }

    // UI 렌더링용 데이터 변환
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

    // summary 계산
    private UsageIncreaseSummary calculateIncrease(
            Map<Integer, UsageAggregate> base, Map<Integer, UsageAggregate> compare, int assetCount, int currentMonth) {

        List<Integer> validMonths = IntStream.rangeClosed(1, 12)
                .filter(m -> m < currentMonth)
                .filter(m -> base.getOrDefault(m, empty()).getReservedUsage() > 0)
                .filter(m -> compare.getOrDefault(m, empty()).getReservedUsage() > 0)
                .boxed()
                .toList();

        if (validMonths.isEmpty()) {
            return UsageIncreaseSummary.builder()
                    .usageRateIncrease(0.0)
                    .actualUsageIncrease(0.0)
                    .resourceUtilizationIncrease(0.0)
                    .build();
        }

        int baseActual = validMonths.stream()
                .mapToInt(m -> base.getOrDefault(m, empty()).getActualUsage())
                .sum();

        int compareActual = validMonths.stream()
                .mapToInt(m -> compare.getOrDefault(m, empty()).getActualUsage())
                .sum();

        int baseReserved = validMonths.stream()
                .mapToInt(m -> base.getOrDefault(m, empty()).getReservedUsage())
                .sum();

        int compareReserved = validMonths.stream()
                .mapToInt(m -> compare.getOrDefault(m, empty()).getReservedUsage())
                .sum();

        if (baseReserved == 0 || compareReserved == 0) {
            return UsageIncreaseSummary.builder()
                    .usageRateIncrease(0.0)
                    .actualUsageIncrease(0.0)
                    .resourceUtilizationIncrease(0.0)
                    .build();
        }

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

    private UsageAggregate empty() {
        return UsageAggregate.builder().actualUsage(0).reservedUsage(0).build();
    }

    private double calcUsageRate(UsageAggregate agg) {
        if (agg.getReservedUsage() == 0) return 0.0;
        return (double) agg.getActualUsage() / agg.getReservedUsage() * 100.0;
    }

    private double round1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private String buildCacheKey(int year, int month, Long assetId, String assetName) {
        String assetKey;

        if (assetId != null) {
            assetKey = "id-" + assetId;
        } else if (assetName != null && !assetName.isBlank()) {
            assetKey = "name-" + assetName.toLowerCase();
        } else {
            assetKey = "all";
        }

        return String.format("usageTrend:%d:%d:%s", year, month, assetKey);
    }
}
