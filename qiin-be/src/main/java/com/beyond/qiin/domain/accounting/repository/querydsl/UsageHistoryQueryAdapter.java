package com.beyond.qiin.domain.accounting.repository.querydsl;

import com.beyond.qiin.domain.accounting.dto.usage_history.request.UsageHistorySearchRequestDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryDetailResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryListResponseDto;
import org.springframework.data.domain.Page;

public interface UsageHistoryQueryAdapter {

    // 목록 조회
    Page<UsageHistoryListResponseDto> searchUsageHistory(final UsageHistorySearchRequestDto req);

    // 상세 조회
    UsageHistoryDetailResponseDto getUsageHistoryDetail(final Long usageHistoryId);
}
