package com.beyond.qiin.domain.accounting.service.query;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.request.UsageHistorySearchRequest;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryResponse;
import com.beyond.qiin.domain.accounting.repository.UsageHistoryQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsageHistoryQueryServiceImpl implements UsageHistoryQueryService {

    private final UsageHistoryQueryRepository usageHistoryQueryRepository;

    @Override
    public PageResponseDto<UsageHistoryResponse> getUsageHistoryList(UsageHistorySearchRequest req) {

        Page<UsageHistoryResponse> page =
                usageHistoryQueryRepository.searchUsageHistory(req);

        return PageResponseDto.from(page);
    }
}
