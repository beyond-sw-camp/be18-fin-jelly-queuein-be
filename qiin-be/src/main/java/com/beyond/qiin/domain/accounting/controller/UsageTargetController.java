package com.beyond.qiin.domain.accounting.controller;

import com.beyond.qiin.domain.accounting.dto.usage_target.request.UsageTargetCreateRequestDto;
import com.beyond.qiin.domain.accounting.dto.usage_target.response.UsageTargetResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_target.response.UsageTargetStatusResponseDto;
import com.beyond.qiin.domain.accounting.service.command.UsageTargetCommandService;
import com.beyond.qiin.domain.accounting.service.query.UsageTargetQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounting/usage-targets")
@RequiredArgsConstructor
public class UsageTargetController {

    private final UsageTargetQueryService usageTargetQueryService;
    private final UsageTargetCommandService usageTargetCommandService;

    // 1) 올해 목표 존재 여부 조회 (Query)
    @GetMapping("/current")
    public UsageTargetStatusResponseDto getCurrentYearStatus() {
        return usageTargetQueryService.getCurrentYearStatus();
    }

    // 2) 목표 등록 (Command)
    @PostMapping
    public UsageTargetResponseDto create(@RequestBody @Valid UsageTargetCreateRequestDto request) {
        return usageTargetCommandService.createTarget(request);
    }

    // 3) 특정 연도 조회 (Query)
    @GetMapping("/{year}")
    public UsageTargetResponseDto getByYear(@PathVariable Integer year) {
        return usageTargetQueryService.getByYear(year);
    }
}
