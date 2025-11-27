package com.beyond.qiin.domain.accounting.dto.settlement.response.raw;

import java.math.BigDecimal;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SettlementPerformanceRawDto {

    private Long assetId; // 자원 ID
    private String assetName; // 자원명

    private Map<Integer, BigDecimal> baseYearData; // 기준연도: 1~12월 절감액 합계
    private Map<Integer, BigDecimal> compareYearData; // 비교연도: 1~12월 절감액 합계
}
