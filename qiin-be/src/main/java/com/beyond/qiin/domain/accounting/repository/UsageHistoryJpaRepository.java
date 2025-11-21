package com.beyond.qiin.domain.accounting.repository;

import com.beyond.qiin.domain.accounting.dto.usage_history.request.UsageHistorySearchRequest;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryResponse;
import org.springframework.data.domain.Page;

public interface UsageHistoryJpaRepository {

    Page<UsageHistoryResponse> searchUsageHistory(UsageHistorySearchRequest req);
}
