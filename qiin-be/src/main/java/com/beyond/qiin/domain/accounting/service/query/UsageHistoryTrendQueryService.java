package com.beyond.qiin.domain.accounting.service.query;

import com.beyond.qiin.domain.accounting.dto.common.ReportingComparisonRequestDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryTrendResponseDto;

public interface UsageHistoryTrendQueryService {

    UsageHistoryTrendResponseDto getUsageHistoryTrend(ReportingComparisonRequestDto request);
}
