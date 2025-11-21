package com.beyond.qiin.domain.accounting.service.query;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.request.UsageHistorySearchRequest;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryResponse;
import com.beyond.qiin.domain.accounting.repository.UsageHistoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsageHistoryQueryServiceImpl implements UsageHistoryQueryService {

    private final UsageHistoryJpaRepository usageHistoryQueryRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<UsageHistoryResponse> getUsageHistoryList(UsageHistorySearchRequest req) {

        Page<UsageHistoryResponse> page = usageHistoryQueryRepository.searchUsageHistory(req);

        return PageResponseDto.from(page);
    }
}
