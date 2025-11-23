package com.beyond.qiin.domain.accounting.controller;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.request.UsageHistorySearchRequestDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryDetailResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryListResponseDto;
import com.beyond.qiin.domain.accounting.service.query.UsageHistoryQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/accounting/usage-history")
@RequiredArgsConstructor
public class UsageHistoryQueryController {

    private final UsageHistoryQueryService service;

    @GetMapping
    public ResponseEntity<PageResponseDto<UsageHistoryListResponseDto>> list(UsageHistorySearchRequestDto req) {
        return ResponseEntity.ok(service.getUsageHistoryList(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsageHistoryDetailResponseDto> detail(@PathVariable Long id) {
        return ResponseEntity.ok(service.getUsageHistoryDetail(id));
    }
}

