package com.beyond.qiin.domain.accounting.repository.querydsl;

import com.beyond.qiin.domain.accounting.dto.usage_history.response.raw.UsageHistoryTrendRawDto;

public interface UsageHistoryTrendQueryAdapter {

    UsageHistoryTrendRawDto getTrendData(int baseYear, int compareYear, Long assetId, String assetName, int months);
}
