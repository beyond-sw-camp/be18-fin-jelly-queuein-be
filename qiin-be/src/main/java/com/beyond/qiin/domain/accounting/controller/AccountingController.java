package com.beyond.qiin.domain.accounting.controller;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.accounting.dto.common.ReportingComparisonRequestDto;
import com.beyond.qiin.domain.accounting.dto.settlement.response.SettlementPerformanceResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.request.UsageHistoryListSearchRequestDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryDetailResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryListResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryTrendResponseDto;
import com.beyond.qiin.domain.accounting.service.query.SettlementPerformanceQueryService;
import com.beyond.qiin.domain.accounting.service.query.UsageHistoryQueryService;
import com.beyond.qiin.domain.accounting.service.query.UsageHistoryTrendQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounting")
@RequiredArgsConstructor
public class AccountingController {

    private final UsageHistoryQueryService usageHistoryService;
    private final UsageHistoryTrendQueryService usageHistoryTrendService;
    private final SettlementPerformanceQueryService settlementPerformanceService;

    // 사용 이력 API

    @GetMapping("/usage-history")
    public ResponseEntity<PageResponseDto<UsageHistoryListResponseDto>> listUsageHistory(
            @ModelAttribute UsageHistoryListSearchRequestDto req, @PageableDefault(size = 20) Pageable pageable) {

        return ResponseEntity.ok(usageHistoryService.getUsageHistoryList(req, pageable));
    }

    @GetMapping("/usage-history/{id}")
    public ResponseEntity<UsageHistoryDetailResponseDto> usageHistoryDetail(@PathVariable Long id) {

        return ResponseEntity.ok(usageHistoryService.getUsageHistoryDetail(id));
    }

    @GetMapping("/usage-history/trend")
    public ResponseEntity<UsageHistoryTrendResponseDto> usageTrend(@ModelAttribute ReportingComparisonRequestDto req) {

        return ResponseEntity.ok(usageHistoryTrendService.getUsageHistoryTrend(req));
    }

    // 정산

    @GetMapping("/settlement/performance")
    public ResponseEntity<SettlementPerformanceResponseDto> settlementPerformance(
            @ModelAttribute ReportingComparisonRequestDto req) {
        return ResponseEntity.ok(settlementPerformanceService.getPerformance(req));
    }
}
