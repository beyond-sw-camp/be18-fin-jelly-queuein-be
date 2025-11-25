package com.beyond.qiin.domain.iam.controller;

import com.beyond.qiin.domain.iam.service.command.PermissionCommandService;
import com.beyond.qiin.domain.iam.service.query.PermissionQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionCommandService permissionCommandService;
    private final PermissionQueryService permissionQueryService;

    // 권한 생성
    @PostMapping
    @PreAuthorize("hasAuthority('MASTER')")
    public ResponseEntity<PermissionResponseDto> createPermission(
            @Valid @RequestBody final CreatePermissionRequestDto request) {
        return ResponseEntity.ok(permissionCommandService.createPermission(request));
    }
}
