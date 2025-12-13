package com.beyond.qiin.domain.accounting.service.query;

import com.beyond.qiin.domain.accounting.dto.common.request.ReportingComparisonRequestDto;
import com.beyond.qiin.domain.accounting.dto.settlement.response.SettlementPerformanceResponseDto;
import com.beyond.qiin.domain.accounting.dto.settlement.response.raw.SettlementPerformanceRawDto;
import com.beyond.qiin.domain.accounting.repository.querydsl.SettlementPerformanceQueryAdapter;
import com.beyond.qiin.infra.redis.accounting.settlement.SettlementPerformanceMonthRedisAdapter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SettlementPerformanceQueryServiceImpl implements SettlementPerformanceQueryService {

    private final SettlementPerformanceQueryAdapter queryAdapter;
    private final SettlementPerformanceMonthRedisAdapter redisAdapter;

    @Override
    public SettlementPerformanceResponseDto getPerformance(ReportingComparisonRequestDto req) {

        LocalDate now = LocalDate.now();
        int nowYear = now.getYear();
        int nowMonth = now.getMonthValue();

        int baseYear = req.getBaseYear() != null ? req.getBaseYear() : nowYear - 1;
        int compareYear = req.getCompareYear() != null ? req.getCompareYear() : nowYear;

        String assetName = req.getAssetName();
        Long assetId = resolveAssetIdFromName(assetName);

        if (assetName != null && !assetName.isBlank() && assetId == null) {
            throw new IllegalArgumentException("자원명을 잘못 입력하셨습니다.");
        }

        // DB 조회 (한 번만)
        SettlementPerformanceRawDto raw =
                queryAdapter.getMonthlyPerformance(baseYear, compareYear, assetId, assetName);

        Map<Integer, BigDecimal> baseMap =
                Optional.ofNullable(raw)
                        .map(SettlementPerformanceRawDto::getBaseYearData)
                        .orElse(Collections.emptyMap());

        Map<Integer, BigDecimal> compareMap =
                Optional.ofNullable(raw)
                        .map(SettlementPerformanceRawDto::getCompareYearData)
                        .orElse(Collections.emptyMap());

        List<SettlementPerformanceResponseDto.MonthlyPerformance> monthlyList = new ArrayList<>();

        // ===== 월별 데이터 처리 =====
        for (int month = 1; month <= 12; month++) {

            BigDecimal baseValue =
                    resolveMonthlyValue(baseYear, month, assetId, baseMap, nowYear, nowMonth);

            BigDecimal compareValue =
                    resolveMonthlyValue(compareYear, month, assetId, compareMap, nowYear, nowMonth);

            monthlyList.add(
                    SettlementPerformanceResponseDto.MonthlyPerformance.builder()
                            .month(month)
                            .baseYearSaving(baseValue)
                            .compareYearSaving(compareValue)
                            .build());
        }

        // ===== 연도 총합 =====
        BigDecimal baseYearTotal = resolveYearTotal(baseYear, assetId, baseMap, nowYear);
        BigDecimal compareYearTotal = resolveYearTotal(compareYear, assetId, compareMap, nowYear);

        // ===== 누적 총합 (항상 DB) =====
        BigDecimal accumulated = queryAdapter.getTotalSavingAllTime();

        return SettlementPerformanceResponseDto.builder()
                .asset(new SettlementPerformanceResponseDto.AssetInfo(assetId, assetName))
                .yearRange(new SettlementPerformanceResponseDto.YearRangeInfo(baseYear, compareYear, 12))
                .monthlyData(monthlyList)
                .summary(new SettlementPerformanceResponseDto.PerformanceSummary(
                        baseYearTotal, compareYearTotal, accumulated))
                .build();
    }

    // =========================
    // 월별 캐시 처리
    // =========================
    private BigDecimal resolveMonthlyValue(
            int year,
            int month,
            Long assetId,
            Map<Integer, BigDecimal> source,
            int nowYear,
            int nowMonth) {

        boolean isCurrentMonth = (year == nowYear && month == nowMonth);
        String assetKey = assetId != null ? "id-" + assetId : "all";
        String cacheKey = "settlementPerformance:%d:%d:%s".formatted(year, month, assetKey);

        if (!isCurrentMonth) {
            BigDecimal cached = redisAdapter.get(cacheKey);
            if (cached != null) {
                return cached;
            }
        }

        BigDecimal value = source.getOrDefault(month, BigDecimal.ZERO);

        if (!isCurrentMonth) {
            redisAdapter.save(cacheKey, value);
        }

        return value;
    }

    // =========================
    // 연도 총합 캐시 처리
    // =========================
    private BigDecimal resolveYearTotal(
            int year,
            Long assetId,
            Map<Integer, BigDecimal> monthlyMap,
            int nowYear) {

        // 올해는 캐싱하지 않음
        if (year == nowYear) {
            return sum(monthlyMap);
        }

        String assetKey = assetId != null ? "id-" + assetId : "all";
        String cacheKey = "settlementPerformance:year:%d:%s".formatted(year, assetKey);

        BigDecimal cached = redisAdapter.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        BigDecimal total = sum(monthlyMap);
        redisAdapter.save(cacheKey, total);
        return total;
    }

    private BigDecimal sum(Map<Integer, BigDecimal> map) {
        return map.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Long resolveAssetIdFromName(String assetName) {
        return queryAdapter.getAssetIdByName(assetName);
    }
}
