package com.beyond.qiin.domain.accounting.service.query;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.request.UsageHistorySearchRequestDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryDetailResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryListResponseDto;
import com.beyond.qiin.domain.accounting.repository.querydsl.UsageHistoryQueryAdapter;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsageHistoryQueryServiceImpl implements UsageHistoryQueryService {

    private final UsageHistoryQueryAdapter usageHistoryQueryAdapter;

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<UsageHistoryListResponseDto> getUsageHistoryList(UsageHistorySearchRequestDto req) {

        Page<UsageHistoryListResponseDto> page = usageHistoryQueryAdapter.searchUsageHistory(req);

        page.forEach(item -> {
            item.changeReservationDurationText(convertMinutes(item.getReservationMinutes()));
            item.changeActualDurationText(convertMinutes(item.getActualMinutes()));
            item.changeUsageRatioText(formatRatio(item.getUsageRatioRaw()));
        });

        return PageResponseDto.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public UsageHistoryDetailResponseDto getUsageHistoryDetail(Long usageHistoryId) {
        return usageHistoryQueryAdapter.getUsageHistoryDetail(usageHistoryId);
    }

    // ---------------------------------------
    // 변환 Helper
    // ---------------------------------------
    private String convertMinutes(Integer minutes) {
        if (minutes == null) return "-";

        int h = minutes / 60;
        int m = minutes % 60;

        if (h > 0 && m > 0) return h + "시간 " + m + "분";
        if (h > 0) return h + "시간";
        return m + "분";
    }

    private String formatRatio(BigDecimal ratio) {
        if (ratio == null) return "-";
        return ratio.stripTrailingZeros().toPlainString() + "%";
    }
}
