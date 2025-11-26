package com.beyond.qiin.domain.accounting.service.query;

import com.beyond.qiin.domain.accounting.dto.usage_history.request.UsageHistoryTrendRequestDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryTrendResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryTrendResponseDto.*;
import com.beyond.qiin.domain.accounting.repository.querydsl.UsageHistoryTrendQueryRepository;
import com.beyond.qiin.domain.inventory.entity.Asset;
import com.beyond.qiin.domain.inventory.repository.AssetRepository;
import java.time.LocalDate;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsageHistoryTrendQueryServiceImpl implements UsageHistoryTrendQueryService {

    private final UsageHistoryTrendQueryRepository trendRepository;
    private final AssetRepository assetRepository;

    @Override
    public UsageHistoryTrendResponseDto getUsageHistoryTrend(UsageHistoryTrendRequestDto request) {

        // 1. 기본 연도 설정
        int currentYear = LocalDate.now().getYear();
        int compareYear = request.getCompareYear() != null ? request.getCompareYear() : currentYear;
        int baseYear = request.getBaseYear() != null ? request.getBaseYear() : compareYear - 1;

        // 2. 비교할 월수 설정 (compareYear 기준)
        int months = (compareYear == currentYear) ? LocalDate.now().getMonthValue() - 1 : 12;

        if (months <= 0) months = 1;

        // 3. 자원 조회 (ID 우선 → 이름 검색)
        Asset asset = resolveAsset(request);

        // 4. QueryDSL로 월별 사용량 집계 조회
        Map<Integer, UsageAggregate> baseYearData = trendRepository.getMonthlyUsage(baseYear, asset.getId(), months);

        Map<Integer, UsageAggregate> compareYearData =
                trendRepository.getMonthlyUsage(compareYear, asset.getId(), months);

        // 5. 월별 사용률 계산
        List<MonthlyUsageData> monthlyUsageData = buildMonthlyUsageData(baseYearData, compareYearData, months);

        // 6. 증가율 계산
        UsageIncreaseSummary summary = calculateIncreaseSummary(baseYearData, compareYearData, months);

        // 7. Response 조립
        return UsageHistoryTrendResponseDto.builder()
                .asset(AssetInfo.builder()
                        .assetId(asset.getId())
                        .assetName(asset.getName())
                        .assetCategory(
                                asset.getCategory() != null
                                        ? asset.getCategory().getName()
                                        : null)
                        .assetImageUrl(asset.getImageUrl())
                        .build())
                .yearRange(YearRangeInfo.builder()
                        .baseYear(baseYear)
                        .compareYear(compareYear)
                        .months(months)
                        .build())
                .monthlyData(monthlyUsageData)
                .summary(summary)
                .build();
    }

    // -------------------------------------------------------
    // 🔸 자원 조회 (ID → AssetName 순)
    // -------------------------------------------------------
    private Asset resolveAsset(UsageHistoryTrendRequestDto request) {
        if (request.getAssetId() != null) {
            return assetRepository
                    .findById(request.getAssetId())
                    .orElseThrow(() -> new IllegalArgumentException("자원을 찾을 수 없습니다."));
        }

        if (request.getAssetName() != null) {
            return assetRepository
                    .findByNameContaining(request.getAssetName())
                    .orElseThrow(() -> new IllegalArgumentException("자원을 찾을 수 없습니다."));
        }

        throw new IllegalArgumentException("assetId 또는 assetName 중 하나는 반드시 필요합니다.");
    }

    // -------------------------------------------------------
    // 🔸 월별 사용률 데이터 계산
    // -------------------------------------------------------
    private List<MonthlyUsageData> buildMonthlyUsageData(
            Map<Integer, UsageAggregate> baseData, Map<Integer, UsageAggregate> compareData, int months) {
        List<MonthlyUsageData> result = new ArrayList<>();

        for (int month = 1; month <= months; month++) {
            UsageAggregate b = baseData.getOrDefault(month, new UsageAggregate());
            UsageAggregate c = compareData.getOrDefault(month, new UsageAggregate());

            Double baseRate = calculateUsageRate(b.actualUsage, b.reservedUsage);
            Double compareRate = calculateUsageRate(c.actualUsage, c.reservedUsage);

            result.add(MonthlyUsageData.builder()
                    .month(month)
                    .baseYearUsageRate(baseRate)
                    .compareYearUsageRate(compareRate)
                    .build());
        }
        return result;
    }

    // -------------------------------------------------------
    // 🔸 단일 월 사용률 계산
    // -------------------------------------------------------
    private Double calculateUsageRate(int actual, int reserved) {
        if (reserved == 0) return 0.0;
        return (actual * 100.0) / reserved;
    }

    // -------------------------------------------------------
    // 🔸 증가율 3종 계산
    // -------------------------------------------------------
    private UsageIncreaseSummary calculateIncreaseSummary(
            Map<Integer, UsageAggregate> base, Map<Integer, UsageAggregate> compare, int months) {

        int baseActual = base.values().stream().mapToInt(a -> a.actualUsage).sum();
        int baseReserved = base.values().stream().mapToInt(a -> a.reservedUsage).sum();
        int compareActual =
                compare.values().stream().mapToInt(a -> a.actualUsage).sum();
        int compareReserved =
                compare.values().stream().mapToInt(a -> a.reservedUsage).sum();

        // 사용률 증가율
        double baseRate = calculateUsageRate(baseActual, baseReserved);
        double compareRate = calculateUsageRate(compareActual, compareReserved);
        double usageRateIncrease = calcRateIncrease(baseRate, compareRate);

        // 실사용 증가율
        double actualUsageIncrease = calcRateIncrease(baseActual / (double) months, compareActual / (double) months);

        // 자원 활용도 증가율 (예약사용 시간 기반)
        double resourceUtilizationIncrease =
                calcRateIncrease(baseReserved / (double) months, compareReserved / (double) months);

        return UsageIncreaseSummary.builder()
                .usageRateIncrease(usageRateIncrease)
                .actualUsageIncrease(actualUsageIncrease)
                .resourceUtilizationIncrease(resourceUtilizationIncrease)
                .build();
    }

    private double calcRateIncrease(double base, double compare) {
        if (base == 0) return compare == 0 ? 0 : 100;
        return ((compare - base) / base) * 100.0;
    }

    // -------------------------------------------------------
    // 🔸 내부 집계 구조 (QueryDSL 반환값)
    // -------------------------------------------------------
    public static class UsageAggregate {
        public int actualUsage = 0;
        public int reservedUsage = 0;
    }
}
