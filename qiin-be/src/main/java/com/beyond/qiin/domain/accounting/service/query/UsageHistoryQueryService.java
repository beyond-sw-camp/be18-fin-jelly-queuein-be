package com.beyond.qiin.domain.accounting.service.query;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.request.UsageHistorySearchRequestDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryDetailResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryListResponseDto;
import org.springframework.data.domain.Pageable;

public interface UsageHistoryQueryService {

    PageResponseDto<UsageHistoryListResponseDto> getUsageHistoryList(
            UsageHistorySearchRequestDto req,
            Pageable pageable
    );

    UsageHistoryDetailResponseDto getUsageHistoryDetail(final Long usageHistoryId);
}