package com.beyond.qiin.domain.accounting.service.query;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.request.UsageHistorySearchRequest;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryResponse;

public interface UsageHistoryQueryService {

    PageResponseDto<UsageHistoryResponse> getUsageHistoryList(UsageHistorySearchRequest req);
}
