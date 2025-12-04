package com.beyond.qiin.domain.accounting.controller;

import com.beyond.qiin.domain.accounting.dto.common.ReportingComparisonRequestDto;
import com.beyond.qiin.domain.accounting.dto.settlement.request.SettlementQuarterRequestDto;
import com.beyond.qiin.domain.accounting.dto.settlement.response.SettlementPerformanceResponseDto;
import com.beyond.qiin.domain.accounting.dto.settlement.response.raw.SettlementQuarterRowDto;
import com.beyond.qiin.domain.accounting.service.query.SettlementPerformanceQueryService;
import com.beyond.qiin.domain.accounting.service.query.SettlementQuarterQueryService;
import com.beyond.qiin.domain.accounting.util.SettlementExcelWriter;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;
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
    private final SettlementQuarterQueryService settlementQuarterQueryService;
    private final SettlementExcelWriter settlementExcelWriter;

    @GetMapping("/performance")
    public ResponseEntity<SettlementPerformanceResponseDto> settlementPerformance(
            @ModelAttribute ReportingComparisonRequestDto req) {
        return ResponseEntity.ok(settlementPerformanceService.getPerformance(req));
    }

    @GetMapping("/quarter")
    public ResponseEntity<?> quarter(@ModelAttribute SettlementQuarterRequestDto req) {
        if (req.getYear() == null) req.setYear(LocalDate.now().getYear());
        return ResponseEntity.ok(settlementQuarterQueryService.getQuarter(req));
    }

    @GetMapping("/quarter/excel")
    public void downloadQuarterExcel(@ModelAttribute SettlementQuarterRequestDto req, HttpServletResponse response) {
        if (req.getYear() == null) req.setYear(LocalDate.now().getYear());
        List<SettlementQuarterRowDto> rows =
                settlementQuarterQueryService.getQuarter(req).getRows();
        settlementExcelWriter.writeFromQuarterRows(response, rows);
    }
}
