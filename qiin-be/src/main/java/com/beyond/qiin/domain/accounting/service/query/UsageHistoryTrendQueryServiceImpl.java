package com.beyond.qiin.domain.accounting.service.query;

import com.beyond.qiin.domain.accounting.dto.common.request.ReportingComparisonRequestDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryTrendResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryTrendResponseDto.AssetInfo;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryTrendResponseDto.MonthlyUsageData;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryTrendResponseDto.PopularCountDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryTrendResponseDto.PopularGroup;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryTrendResponseDto.PopularTimeDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryTrendResponseDto.YearRangeInfo;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.raw.UsageHistoryTrendRawDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.raw.UsageHistoryTrendRawDto.UsageAggregate;
import com.beyond.qiin.domain.accounting.repository.querydsl.UsageHistoryTrendQueryAdapter;
import com.beyond.qiin.infra.redis.accounting.usage_history.UsageTrendRedisAdapter;
import com.beyond.qiin.infra.redis.accounting.usage_history.UsageTrendTopRedisAdapter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsageHistoryTrendQueryServiceImpl implements UsageHistoryTrendQueryService {

    private final UsageHistoryTrendQueryAdapter trendQueryAdapter;

    private final UsageTrendRedisAdapter redisAdapter; // 월별 사용률 캐싱
    private final UsageTrendTopRedisAdapter topRedisAdapter; // TOP3 캐싱

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public UsageHistoryTrendResponseDto getUsageHistoryTrend(ReportingComparisonRequestDto request) {

        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();

        int baseYear = request.getBaseYear() != null ? request.getBaseYear() : currentYear - 1;
        int compareYear = request.getCompareYear() != null ? request.getCompareYear() : currentYear;
        String assetName = request.getAssetName();

        UsageHistoryTrendRawDto raw = trendQueryAdapter.getTrendData(baseYear, compareYear, assetName);

        Map<Integer, UsageAggregate> baseRaw = raw.getBaseYearData();
        Map<Integer, UsageAggregate> compareRaw = raw.getCompareYearData();

        Map<Integer, Double> baseRate =
                loadUsageRateWithCache(baseYear, currentYear, currentMonth, raw.getAssetId(), baseRaw);
        Map<Integer, Double> compareRate =
                loadUsageRateWithCache(compareYear, currentYear, currentMonth, raw.getAssetId(), compareRaw);

        List<MonthlyUsageData> monthlyList = buildMonthlyList(baseRate, compareRate);

        Double actualUsageIncrease = calcActualUsageIncrease(baseRaw, compareRaw, currentMonth);

        List<PopularCountDto> baseCountTop = getTop3CountWithCache(baseYear, currentYear);
        List<PopularCountDto> compareCountTop = getTop3CountWithCache(compareYear, currentYear);

        List<PopularTimeDto> baseTimeTop = getTop3TimeWithCache(baseYear, currentYear);
        List<PopularTimeDto> compareTimeTop = getTop3TimeWithCache(compareYear, currentYear);

        return UsageHistoryTrendResponseDto.builder()
                .asset(new AssetInfo(raw.getAssetId(), raw.getAssetName()))
                .yearRange(new YearRangeInfo(baseYear, compareYear))
                .monthlyData(monthlyList)
                .actualUsageIncrease(actualUsageIncrease)
                .popularByCount(new PopularGroup<>(baseCountTop, compareCountTop))
                .popularByTime(new PopularGroup<>(baseTimeTop, compareTimeTop))
                .build();
    }

    // -------------------------------
    // TOP3 예약 건수 캐싱
    // -------------------------------
    private List<PopularCountDto> getTop3CountWithCache(int year, int currentYear) {
        String key = "usageTrendTop:%d:count".formatted(year);

        if (year < currentYear) {
            String cached = topRedisAdapter.get(key);
            if (cached != null) {
                try {
                    return objectMapper.readValue(cached, new TypeReference<>() {});
                } catch (Exception ignored) {
                }
            }
        }

        List<PopularCountDto> top3 = trendQueryAdapter.getTopByCount(year).stream()
                .map(r -> new PopularCountDto(r.getAssetId(), r.getAssetName(), r.getCount()))
                .limit(3)
                .toList();

        if (year < currentYear) {
            try {
                topRedisAdapter.save(key, objectMapper.writeValueAsString(top3), null);
            } catch (Exception ignored) {
            }
        }

        return top3;
    }

    // -------------------------------
    // TOP3 예약 시간 캐싱
    // -------------------------------
    private List<PopularTimeDto> getTop3TimeWithCache(int year, int currentYear) {
        String key = "usageTrendTop:%d:time".formatted(year);

        if (year < currentYear) {
            String cached = topRedisAdapter.get(key);
            if (cached != null) {
                try {
                    return objectMapper.readValue(cached, new TypeReference<>() {});
                } catch (Exception ignored) {
                }
            }
        }

        List<PopularTimeDto> top3 = trendQueryAdapter.getTopByTime(year).stream()
                .map(r -> new PopularTimeDto(r.getAssetId(), r.getAssetName(), r.getTotalMinutes()))
                .limit(3)
                .toList();

        if (year < currentYear) {
            try {
                topRedisAdapter.save(key, objectMapper.writeValueAsString(top3), null);
            } catch (Exception ignored) {
            }
        }

        return top3;
    }

    // -------------------------------
    // 월별 사용률 캐싱 (기존 유지)
    // -------------------------------
    private Map<Integer, Double> loadUsageRateWithCache(
            int targetYear, int currentYear, int currentMonth, Long assetId, Map<Integer, UsageAggregate> rawData) {

        Map<Integer, Double> result = new HashMap<>();

        for (int month = 1; month <= 12; month++) {

            boolean isCurrentMonth = targetYear == currentYear && month == currentMonth;
            boolean isPastYear = targetYear < currentYear;
            boolean isPastMonth = targetYear == currentYear && month < currentMonth;

            String key = "usageTrend:%d:%d:%s".formatted(targetYear, month, assetId != null ? "id-" + assetId : "all");

            if (!isCurrentMonth) {
                Double cached = redisAdapter.get(key);
                if (cached != null) {
                    result.put(month, cached);
                    continue;
                }
            }

            UsageAggregate agg = rawData.getOrDefault(month, empty());
            double rate = calcRate(agg);
            result.put(month, rate);

            if (isPastYear || isPastMonth) {
                redisAdapter.save(key, rate, null); // 영구 저장
            }
        }

        return result;
    }

    private Double calcActualUsageIncrease(
            Map<Integer, UsageAggregate> base, Map<Integer, UsageAggregate> compare, int currentMonth) {

        int baseActual = 0, baseReserved = 0;
        int compareActual = 0, compareReserved = 0;

        for (int m = 1; m < currentMonth; m++) {
            UsageAggregate b = base.getOrDefault(m, empty());
            UsageAggregate c = compare.getOrDefault(m, empty());

            baseActual += b.getActualUsage();
            baseReserved += b.getReservedUsage();

            compareActual += c.getActualUsage();
            compareReserved += c.getReservedUsage();
        }

        if (baseReserved == 0 || compareReserved == 0) return 0.0;

        double baseRatio = (double) baseActual / baseReserved;
        double compareRatio = (double) compareActual / compareReserved;

        return Math.round((compareRatio - baseRatio) * 1000) / 10.0;
    }

    private List<MonthlyUsageData> buildMonthlyList(Map<Integer, Double> baseRate, Map<Integer, Double> compareRate) {

        List<MonthlyUsageData> list = new ArrayList<>();

        for (int m = 1; m <= 12; m++) {
            list.add(MonthlyUsageData.builder()
                    .month(m)
                    .baseYearUsageRate(round1(baseRate.getOrDefault(m, 0.0)))
                    .compareYearUsageRate(round1(compareRate.getOrDefault(m, 0.0)))
                    .build());
        }

        return list;
    }

    private UsageAggregate empty() {
        return UsageAggregate.builder().actualUsage(0).reservedUsage(0).build();
    }

    private double calcRate(UsageAggregate agg) {
        if (agg.getReservedUsage() == 0) return 0.0;
        return (double) agg.getActualUsage() / agg.getReservedUsage() * 100.0;
    }

    private double round1(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
