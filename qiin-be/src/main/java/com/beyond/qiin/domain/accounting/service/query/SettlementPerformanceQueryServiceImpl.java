// file: src/main/java/com/beyond/qiin/domain/accounting/service/query/SettlementPerformanceQueryServiceImpl.java
package com.beyond.qiin.domain.accounting.service.query;

import com.beyond.qiin.domain.accounting.dto.common.ReportingComparisonRequestDto;
import com.beyond.qiin.domain.accounting.dto.settlement.response.SettlementPerformanceResponseDto;
import com.beyond.qiin.domain.accounting.dto.settlement.response.raw.SettlementPerformanceRawDto;
import com.beyond.qiin.domain.accounting.repository.querydsl.SettlementPerformanceQueryAdapter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SettlementPerformanceQueryServiceImpl implements SettlementPerformanceQueryService {

    private final SettlementPerformanceQueryAdapter queryAdapter;

    @Override
    public SettlementPerformanceResponseDto getPerformance(ReportingComparisonRequestDto req) {

        int currentYear = LocalDate.now().getYear();

        int baseYear = req.getBaseYear() != null ? req.getBaseYear() : currentYear - 1;
        int compareYear = req.getCompareYear() != null ? req.getCompareYear() : currentYear;

        SettlementPerformanceRawDto raw =
                queryAdapter.getMonthlyPerformance(baseYear, compareYear, req.getAssetId(), req.getAssetName());

        // Null 안전성 확보: raw가 null이면 빈 맵을 사용
        Map<Integer, BigDecimal> baseMap = Optional.ofNullable(raw)
                .map(SettlementPerformanceRawDto::getBaseYearData)
                .orElse(Collections.emptyMap());

        Map<Integer, BigDecimal> compareMap = Optional.ofNullable(raw)
                .map(SettlementPerformanceRawDto::getCompareYearData)
                .orElse(Collections.emptyMap());

        List<SettlementPerformanceResponseDto.MonthlyPerformance> monthlyList = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            monthlyList.add(SettlementPerformanceResponseDto.MonthlyPerformance.builder()
                    .month(m)
                    .baseYearSaving(baseMap.getOrDefault(m, BigDecimal.ZERO))
                    .compareYearSaving(compareMap.getOrDefault(m, BigDecimal.ZERO))
                    .build());
        }

        BigDecimal baseYearTotal = sum(baseMap);
        BigDecimal compareYearTotal = sum(compareMap);

        // 최종 수정: raw가 null일 때 AssetInfo 필드도 Null safe하게 처리 (NPE 방지)
        Long assetId = Optional.ofNullable(raw)
                .map(SettlementPerformanceRawDto::getAssetId)
                .orElse(null);
        String assetName = Optional.ofNullable(raw)
                .map(SettlementPerformanceRawDto::getAssetName)
                .orElse(null);

        // 전체 누적 절감 금액
        BigDecimal accumulated = queryAdapter.getTotalSavingAllTime();

        return SettlementPerformanceResponseDto.builder()
                .asset(new SettlementPerformanceResponseDto.AssetInfo(assetId, assetName)) // ⇐ 수정된 부분
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
