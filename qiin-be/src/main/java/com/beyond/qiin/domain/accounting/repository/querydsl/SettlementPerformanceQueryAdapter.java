package com.beyond.qiin.domain.accounting.repository.querydsl;

import com.beyond.qiin.domain.accounting.dto.settlement.response.raw.SettlementPerformanceRawDto;

public interface SettlementPerformanceQueryAdapter {

    SettlementPerformanceRawDto getMonthlyPerformance(int baseYear, int compareYear, Long assetId, String assetName);
}
