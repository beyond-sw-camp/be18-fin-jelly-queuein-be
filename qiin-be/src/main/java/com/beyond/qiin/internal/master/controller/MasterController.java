package com.beyond.qiin.internal.master.controller;

import com.beyond.qiin.internal.master.annotation.RequireInternalMasterKey;
import com.beyond.qiin.internal.master.dto.request.RegisterMasterRequestDto;
import com.beyond.qiin.internal.master.dto.response.RegisterMasterResponseDto;
import com.beyond.qiin.internal.master.service.MasterService;
import com.beyond.qiin.internal.master.validator.MasterApiKeyValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/masters")
public class MasterController {

    private final MasterService masterService;
    private final MasterApiKeyValidator masterApiKeyValidator;

    @RequireInternalMasterKey
    @PostMapping
    public ResponseEntity<RegisterMasterResponseDto> createMaster(
            @RequestBody final RegisterMasterRequestDto request, final HttpServletRequest httpRequest) {

        // MASTER 생성
        RegisterMasterResponseDto response = masterService.createMaster(request);

        return ResponseEntity.ok(response);
    }
}
