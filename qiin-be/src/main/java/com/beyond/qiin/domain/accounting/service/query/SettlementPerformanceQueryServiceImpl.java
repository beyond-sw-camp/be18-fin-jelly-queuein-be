package com.beyond.qiin.domain.accounting.service.query;

import com.beyond.qiin.domain.accounting.dto.common.ReportingComparisonRequestDto;
import com.beyond.qiin.domain.accounting.dto.settlement.response.SettlementPerformanceResponseDto;
import com.beyond.qiin.domain.accounting.dto.settlement.response.raw.SettlementPerformanceRawDto;
import com.beyond.qiin.domain.accounting.repository.querydsl.SettlementPerformanceQueryAdapter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SettlementPerformanceQueryServiceImpl implements SettlementPerformanceQueryService {

    private final SettlementPerformanceQueryAdapter queryAdapter;

    @Override
    public SettlementPerformanceResponseDto getPerformance(ReportingComparisonRequestDto req) {

        // 1) 기본값 처리 (UsageHistoryTrend와 동일 기준)
        int currentYear = LocalDate.now().getYear();

        int baseYear = (req.getBaseYear() != null) ? req.getBaseYear() : currentYear - 1;
        int compareYear = (req.getCompareYear() != null) ? req.getCompareYear() : currentYear;

        Long assetId = req.getAssetId();
        String assetName = req.getAssetName();

        // 2) Raw 데이터 조회
        SettlementPerformanceRawDto raw = queryAdapter.getMonthlyPerformance(baseYear, compareYear, assetId, assetName);

        Map<Integer, BigDecimal> baseMap = raw.getBaseYearData();
        Map<Integer, BigDecimal> compareMap = raw.getCompareYearData();

        // 3) Monthly 데이터 생성
        List<SettlementPerformanceResponseDto.MonthlyPerformance> monthlyList = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            monthlyList.add(SettlementPerformanceResponseDto.MonthlyPerformance.builder()
                    .month(m)
                    .baseYearSaving(baseMap.getOrDefault(m, BigDecimal.ZERO))
                    .compareYearSaving(compareMap.getOrDefault(m, BigDecimal.ZERO))
                    .build());
        }

        // 4) Summary 계산
        BigDecimal baseYearTotal = sum(baseMap);
        BigDecimal compareYearTotal = sum(compareMap);
        BigDecimal accumulated = baseYearTotal.add(compareYearTotal);

        // 5) Response 생성
        return SettlementPerformanceResponseDto.builder()
                .asset(new SettlementPerformanceResponseDto.AssetInfo(raw.getAssetId(), raw.getAssetName()))
                .yearRange(new SettlementPerformanceResponseDto.YearRangeInfo(baseYear, compareYear, 12))
                .monthlyData(monthlyList)
                .summary(new SettlementPerformanceResponseDto.PerformanceSummary(
                        baseYearTotal, compareYearTotal, accumulated))
                .build();
    }

    private BigDecimal sum(Map<Integer, BigDecimal> map) {
        return map.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
