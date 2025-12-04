package com.beyond.qiin.domain.accounting.service.query;

import com.beyond.qiin.domain.accounting.dto.settlement.request.SettlementQuarterRequestDto;
import com.beyond.qiin.domain.accounting.dto.settlement.response.SettlementQuarterResponseDto;
import com.beyond.qiin.domain.accounting.dto.settlement.response.raw.SettlementQuarterRowDto;
import com.beyond.qiin.domain.accounting.repository.querydsl.SettlementQuarterQueryAdapter;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SettlementQuarterQueryServiceImpl implements SettlementQuarterQueryService {

    private final SettlementQuarterQueryAdapter settlementQuarterQueryAdapter;

    /**
     * 화면 조회용 분기 정산
     */
    @Override
    public SettlementQuarterResponseDto getQuarter(SettlementQuarterRequestDto req) {
        int year = req.getYear();
        Integer quarter = req.getQuarter();
        String assetName = req.getAssetName();

        List<SettlementQuarterRowDto> rows = settlementQuarterQueryAdapter.getQuarterRows(year, quarter, assetName);

        // DTO 계산 적용
        rows.forEach(this::applyCalculations);

        return SettlementQuarterResponseDto.builder()
                .year(year)
                .quarter(quarter)
                .rows(rows)
                .build();
    }

    /* ===============================
    DTO 계산 유틸
    =============================== */
    private void applyCalculations(SettlementQuarterRowDto dto) {
        int reserved = nz(dto.getReservedHours());
        int actual = nz(dto.getActualHours());
        double performRate = calculatePerformRate(reserved, actual);
        double utilizationRate = calculateUtilizationRate(dto.getYear(), dto.getQuarter(), reserved);

        dto.setPerformRate(performRate);
        dto.setUtilizationRate(utilizationRate);
        dto.setPerformGrade(gradeByRate(performRate));
        dto.setUtilizationGrade(gradeByRate(utilizationRate));

        dto.setTotalUsageCost(nz(dto.getTotalUsageCost()));
        dto.setActualUsageCost(nz(dto.getActualUsageCost()));
        dto.setUsageGapCost(nz(dto.getUsageGapCost()));
    }

    /* -------------------------
    공통 계산 메서드
    ------------------------- */
    private double calculatePerformRate(int reserved, int actual) {
        return reserved == 0 ? 0.0 : (double) actual / reserved;
    }

    private double calculateUtilizationRate(int year, int quarter, int reserved) {
        int availableHours = getDaysInQuarter(year, quarter) * 24;
        return availableHours == 0 ? 0.0 : (double) reserved / availableHours;
    }

    private String gradeByRate(double rate) {
        if (rate >= 0.8) return "A";
        if (rate >= 0.5) return "B";
        return "C";
    }

    private int nz(Integer v) {
        return v == null ? 0 : v;
    }

    private BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private int getDaysInQuarter(int year, int quarter) {
        return switch (quarter) {
            case 1 -> 31 + (isLeap(year) ? 29 : 28) + 31;
            case 2 -> 30 + 31 + 30;
            case 3 -> 31 + 31 + 30;
            case 4 -> 31 + 30 + 31;
            default -> 0;
        };
    }

    private boolean isLeap(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }
}
