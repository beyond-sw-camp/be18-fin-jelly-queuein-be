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

        // QueryAdapter에서 필터링을 처리하므로 여기 값은 그대로 넘김
        String assetName = request.getAssetName();

        // DB raw 조회
        UsageHistoryTrendRawDto raw =
                trendQueryAdapter.getTrendData(baseYear, compareYear, null, assetName, 0);

        Long resolvedAssetId = raw.getAssetId();  // ⭐ 캐싱용으로 반드시 이것을 사용해야 함

        Map<Integer, UsageAggregate> baseRaw =
                Optional.ofNullable(raw).map(UsageHistoryTrendRawDto::getBaseYearData).orElse(Map.of());

        Map<Integer, UsageAggregate> compareRaw =
                Optional.ofNullable(raw).map(UsageHistoryTrendRawDto::getCompareYearData).orElse(Map.of());

        // 월별 사용률 계산 + 캐싱 적용
        Map<Integer, Double> baseRateMap =
                loadYearlyUsageRateWithCache(baseYear, currentYear, currentMonth, resolvedAssetId, baseRaw);

        Map<Integer, Double> compareRateMap =
                loadYearlyUsageRateWithCache(compareYear, currentYear, currentMonth, resolvedAssetId, compareRaw);

        // UI 표시용 리스트
        List<MonthlyUsageData> monthlyList = buildMonthlyList(baseRateMap, compareRateMap);

        int assetCount = Optional.ofNullable(raw).map(UsageHistoryTrendRawDto::getAssetCount).orElse(0);

        // Summary 계산은 raw 데이터 기반 (캐싱 영향 없음)
        UsageIncreaseSummary summary =
                calculateIncrease(baseRaw, compareRaw, assetCount, currentMonth);

        return UsageHistoryTrendResponseDto.builder()
                .asset(new AssetInfo(
                        raw.getAssetId(),
                        raw.getAssetName()
                ))
                .yearRange(new YearRangeInfo(baseYear, compareYear, 12))
                .monthlyData(monthlyList)
                .summary(summary)
                .build();
    }

    // -------------------------------------------------------------------------
    // 월별 사용률 캐싱 처리 — 검색한 자원 ID 기준으로 캐싱됨
    // -------------------------------------------------------------------------
    private Map<Integer, Double> loadYearlyUsageRateWithCache(
            int targetYear,
            int currentYear,
            int currentMonth,
            Long resolvedAssetId,
            Map<Integer, UsageAggregate> rawData
    ) {
        Map<Integer, Double> rateMap = new HashMap<>();

        for (int month = 1; month <= 12; month++) {

            boolean isCurrentMonth = (targetYear == currentYear && month == currentMonth);
            boolean isPastYear = targetYear < currentYear;
            boolean isPastMonthOfThisYear = (targetYear == currentYear && month < currentMonth);

            String key = buildCacheKey(targetYear, month, resolvedAssetId);

            // 캐시 조회 (단, 현재월 제외)
            if (!isCurrentMonth) {
                Double cached = redisAdapter.get(key);
                if (cached != null) {
                    rateMap.put(month, cached);
                    continue;
                }
            }

            // raw 기반 계산
            UsageAggregate agg = rawData.getOrDefault(month, empty());
            double rate = calcUsageRate(agg);
            rateMap.put(month, rate);

            // 캐시 저장 규칙
            if (isPastYear) {
                redisAdapter.save(key, rate, null);     // 영구 캐싱
            } else if (isPastMonthOfThisYear) {
                redisAdapter.save(key, rate, 1L);       // 1시간 캐싱
            }
        }

        return rateMap;
    }

    // -------------------------------------------------------------------------
    // UI 렌더링용 리스트 생성
    // -------------------------------------------------------------------------
    private List<MonthlyUsageData> buildMonthlyList(
            Map<Integer, Double> baseRate,
            Map<Integer, Double> compareRate
    ) {
        List<MonthlyUsageData> result = new ArrayList<>();

        for (int m = 1; m <= 12; m++) {
            result.add(MonthlyUsageData.builder()
                    .month(m)
                    .baseYearUsageRate(round1(baseRate.getOrDefault(m, 0.0)))
                    .compareYearUsageRate(round1(compareRate.getOrDefault(m, 0.0)))
                    .build());
        }

        return result;
    }

    // -------------------------------------------------------------------------
    // Summary 계산 (raw 데이터 기반)
    // -------------------------------------------------------------------------
    private UsageIncreaseSummary calculateIncrease(
            Map<Integer, UsageAggregate> base,
            Map<Integer, UsageAggregate> compare,
            int assetCount,
            int currentMonth
    ) {

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
                .mapToInt(m -> base.getOrDefault(m, empty()).getActualUsage()).sum();

        int compareActual = validMonths.stream()
                .mapToInt(m -> compare.getOrDefault(m, empty()).getActualUsage()).sum();

        int baseReserved = validMonths.stream()
                .mapToInt(m -> base.getOrDefault(m, empty()).getReservedUsage()).sum();

        int compareReserved = validMonths.stream()
                .mapToInt(m -> compare.getOrDefault(m, empty()).getReservedUsage()).sum();

        double usageRateIncrease =
                ((double) (compareReserved - baseReserved) / baseReserved) * 100.0;

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

    /**
     * 캐시 키 = year:month:assetId|null(전체)
     */
    private String buildCacheKey(int year, int month, Long assetId) {

        String assetKey = (assetId != null)
                ? "id-" + assetId
                : "all";

        return String.format("usageTrend:%d:%d:%s", year, month, assetKey);
    }

}
