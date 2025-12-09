package com.beyond.qiin.domain.accounting.service.query;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.accounting.dto.common.response.YearListResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.request.UsageHistoryListSearchRequestDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryDetailResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryListResponseDto;
import com.beyond.qiin.domain.accounting.repository.UsageHistoryJpaRepository;
import com.beyond.qiin.domain.accounting.repository.querydsl.UsageHistoryQueryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsageHistoryQueryServiceImpl implements UsageHistoryQueryService {

    private final UsageHistoryQueryAdapter usageHistoryQueryAdapter;
    private final UsageHistoryJpaRepository usageHistoryJpaRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<UsageHistoryListResponseDto> getUsageHistoryList(
            final UsageHistoryListSearchRequestDto req, final Pageable pageable) {

        Page<UsageHistoryListResponseDto> rawPage = usageHistoryQueryAdapter.searchUsageHistory(req, pageable);

        // 가공 없이 그대로 반환
        return PageResponseDto.from(rawPage);
    }

    @Override
    @Transactional(readOnly = true)
    public UsageHistoryDetailResponseDto getUsageHistoryDetail(final Long usageHistoryId) {
        return usageHistoryQueryAdapter.getUsageHistoryDetail(usageHistoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public YearListResponseDto getExistingYears() {
        return new YearListResponseDto(usageHistoryJpaRepository.findExistingYears());
    }
}
