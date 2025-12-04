package com.beyond.qiin.domain.accounting.repository.querydsl;

import com.beyond.qiin.domain.accounting.dto.settlement.response.raw.SettlementPerformanceRawDto;
import java.math.BigDecimal;

public interface SettlementPerformanceQueryAdapter {

    SettlementPerformanceRawDto getMonthlyPerformance(int baseYear, int compareYear, Long assetId, String assetName);

    // 누적 절감 금액
    BigDecimal getTotalSavingAllTime();
}
