package com.beyond.qiin.domain.accounting.controller;

import com.beyond.qiin.domain.accounting.dto.common.ReportingComparisonRequestDto;
import com.beyond.qiin.domain.accounting.dto.settlement.response.SettlementPerformanceResponseDto;
import com.beyond.qiin.domain.accounting.service.query.SettlementPerformanceQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/accounting/settlement")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementPerformanceQueryService settlementPerformanceService;

    @GetMapping("/performance")
    public ResponseEntity<SettlementPerformanceResponseDto> settlementPerformance(
            @ModelAttribute ReportingComparisonRequestDto req) {
        return ResponseEntity.ok(settlementPerformanceService.getPerformance(req));
    }
}
