package com.beyond.qiin.domain.accounting.service.query;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.request.UsageHistorySearchRequestDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryDetailResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryListResponseDto;

public interface UsageHistoryQueryService {

    // 목록 조회
    PageResponseDto<UsageHistoryListResponseDto> getUsageHistoryList(final UsageHistorySearchRequestDto req);

    // 상세 조회
    UsageHistoryDetailResponseDto getUsageHistoryDetail(final Long usageHistoryId);
}
