package com.beyond.qiin.domain.accounting.controller;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.request.UsageHistoryListSearchRequestDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.request.UsageHistoryTrendRequestDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryDetailResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryListResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryTrendResponseDto;
import com.beyond.qiin.domain.accounting.service.query.UsageHistoryQueryService;
import com.beyond.qiin.domain.accounting.service.query.UsageHistoryTrendQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounting/usage-history")
@RequiredArgsConstructor
public class UsageHistoryController {

    private final UsageHistoryQueryService service;
    private final UsageHistoryTrendQueryService trendService;

    @GetMapping
    public ResponseEntity<PageResponseDto<UsageHistoryListResponseDto>> list(
            @ModelAttribute UsageHistoryListSearchRequestDto req, @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(service.getUsageHistoryList(req, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsageHistoryDetailResponseDto> detail(@PathVariable Long id) {
        return ResponseEntity.ok(service.getUsageHistoryDetail(id));
    }

    @GetMapping("/trend")
    public ResponseEntity<UsageHistoryTrendResponseDto> trend(@ModelAttribute UsageHistoryTrendRequestDto req) {
        return ResponseEntity.ok(trendService.getUsageHistoryTrend(req));
    }
}
