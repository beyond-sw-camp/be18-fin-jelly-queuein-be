package com.beyond.qiin.domain.accounting.repository.querydsl;

import com.beyond.qiin.domain.accounting.dto.usage_history.response.raw.UsageHistoryTrendPopularCountDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.raw.UsageHistoryTrendPopularTimeDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.raw.UsageHistoryTrendRawDto;
import java.util.List;

public interface UsageHistoryTrendQueryAdapter {

    // 월별 raw 데이터 조회
    UsageHistoryTrendRawDto getTrendData(int baseYear, int compareYear, String assetName);

    // 연도별 예약 건수 TOP
    List<UsageHistoryTrendPopularCountDto> getTopByCount(int year);

    // 연도별 예약 시간 TOP
    List<UsageHistoryTrendPopularTimeDto> getTopByTime(int year);
}
