package com.beyond.qiin.domain.accounting.service.query;

import com.beyond.qiin.domain.accounting.dto.common.request.ReportingComparisonRequestDto;
import com.beyond.qiin.domain.accounting.dto.settlement.response.SettlementPerformanceResponseDto;

public interface SettlementPerformanceQueryService {

    SettlementPerformanceResponseDto getPerformance(ReportingComparisonRequestDto req);
}
