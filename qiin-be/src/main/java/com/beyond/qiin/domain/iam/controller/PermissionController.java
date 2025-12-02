package com.beyond.qiin.domain.iam.controller;

import com.beyond.qiin.domain.iam.dto.permission.request.CreatePermissionRequestDto;
import com.beyond.qiin.domain.iam.dto.permission.request.UpdatePermissionRequestDto;
import com.beyond.qiin.domain.iam.dto.permission.response.PermissionListResponseDto;
import com.beyond.qiin.domain.iam.dto.permission.response.PermissionResponseDto;
import com.beyond.qiin.domain.iam.service.command.PermissionCommandService;
import com.beyond.qiin.domain.iam.service.query.PermissionQueryService;
import com.beyond.qiin.security.resolver.CurrentUserId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionCommandService permissionCommandService;
    private final PermissionQueryService permissionQueryService;

    // -------------------------------------------
    // Command
    // -------------------------------------------
    // 권한 생성
    @PostMapping
    @PreAuthorize("hasAuthority('MASTER')")
    public ResponseEntity<PermissionResponseDto> createPermission(
            @Valid @RequestBody final CreatePermissionRequestDto request) {
        return ResponseEntity.ok(permissionCommandService.createPermission(request));
    }

    // 권한 수정
    @PutMapping("/{permissionId}")
    @PreAuthorize("hasAuthority('MASTER')")
    public ResponseEntity<PermissionResponseDto> updatePermission(
            @PathVariable final Long permissionId, @Valid @RequestBody final UpdatePermissionRequestDto request) {
        return ResponseEntity.ok(permissionCommandService.updatePermission(permissionId, request));
    }

    // 권한 삭제
    @DeleteMapping("/{permissionId}")
    @PreAuthorize("hasAuthority('MASTER')")
    public ResponseEntity<Void> deletePermission(
            @PathVariable final Long permissionId, @CurrentUserId final Long deleterId) {
        permissionCommandService.deletePermission(permissionId, deleterId);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------
    // Query
    // -------------------------------------------

    // 권한 상세 조회
    @GetMapping("/{permissionId}")
    @PreAuthorize("hasAnyAuthority('MASTER','ADMIN')")
    public ResponseEntity<PermissionResponseDto> getPermission(@PathVariable final Long permissionId) {
        return ResponseEntity.ok(permissionQueryService.getPermission(permissionId));
    }

    // 권한 목록 조회
    @GetMapping
    @PreAuthorize("hasAnyAuthority('MASTER','ADMIN')")
    public ResponseEntity<PermissionListResponseDto> getPermissions() {
        return ResponseEntity.ok(permissionQueryService.getPermissions());
    }
}
