package com.beyond.qiin.domain.accounting.controller.query;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.request.UsageHistorySearchRequest;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryResponse;
import com.beyond.qiin.domain.accounting.service.query.UsageHistoryQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/accounting/usage-history")
@RequiredArgsConstructor
public class UsageHistoryQueryController {

    private final UsageHistoryQueryService usageHistoryQueryService;

    @GetMapping
    public ResponseEntity<PageResponseDto<UsageHistoryResponse>> getUsageHistoryList(UsageHistorySearchRequest req) {
        PageResponseDto<UsageHistoryResponse> response = usageHistoryQueryService.getUsageHistoryList(req);

        return ResponseEntity.ok(response);
    }
}
