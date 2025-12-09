package com.beyond.qiin.domain.iam.controller;

import com.beyond.qiin.domain.iam.dto.role.request.CreateRoleRequestDto;
import com.beyond.qiin.domain.iam.dto.role.request.UpdateRoleRequestDto;
import com.beyond.qiin.domain.iam.dto.role.response.RoleListResponseDto;
import com.beyond.qiin.domain.iam.dto.role.response.RoleResponseDto;
import com.beyond.qiin.domain.iam.dto.role_permission.request.AddRolePermissionsRequestDto;
import com.beyond.qiin.domain.iam.dto.role_permission.request.ReplaceRolePermissionsRequestDto;
import com.beyond.qiin.domain.iam.dto.role_permission.response.RolePermissionListResponseDto;
import com.beyond.qiin.domain.iam.service.command.RoleCommandService;
import com.beyond.qiin.domain.iam.service.command.RolePermissionCommandService;
import com.beyond.qiin.domain.iam.service.query.RoleQueryService;
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
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleCommandService roleCommandService;
    private final RolePermissionCommandService rolePermissionCommandService;

    private final RoleQueryService roleQueryService;

    // -------------------------------------------
    // Command
    // -------------------------------------------
    // Role Command
    // -----------------------

    // 역할 생성
    @PostMapping
    @PreAuthorize("hasAuthority('IAM_ROLE_CREATE')")
    public ResponseEntity<RoleResponseDto> createRole(@Valid @RequestBody final CreateRoleRequestDto request) {
        return ResponseEntity.ok(roleCommandService.createRole(request));
    }

    // REVIEW: PUT 유지하고 2개의 필드 무조건 보내게 하는거 프론트에서 검증하게 하며,
    // 500 안 던지게 하기
    // 역할 수정
    @PutMapping("/{roleId}")
    @PreAuthorize("hasAuthority('IAM_ROLE_UPDATE')")
    public ResponseEntity<RoleResponseDto> updateRole(
            @PathVariable final Long roleId, @Valid @RequestBody final UpdateRoleRequestDto request) {
        return ResponseEntity.ok(roleCommandService.updateRole(roleId, request));
    }

    // 역할 삭제
    @DeleteMapping("/{roleId}")
    @PreAuthorize("hasAuthority('IAM_ROLE_DELETE')")
    public ResponseEntity<Void> deleteRole(@PathVariable final Long roleId, @CurrentUserId final Long deleterId) {
        roleCommandService.deleteRole(roleId, deleterId);
        return ResponseEntity.noContent().build();
    }

    // -----------------------
    // RolePermission Command
    // -----------------------

    // 1개의 역할에 여러 권한 추가
    @PostMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('IAM_ROLE_PERMISSION_ADD')")
    public ResponseEntity<Void> addPermissions(
            @PathVariable final Long roleId, @RequestBody final AddRolePermissionsRequestDto request) {

        rolePermissionCommandService.addPermissions(roleId, request.getPermissionIds());
        return ResponseEntity.ok().build();
    }

    // 역할-권한 매핑 수정
    @PutMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('IAM_ROLE_PERMISSION_REPLACE')")
    public ResponseEntity<RolePermissionListResponseDto> replacePermissions(
            @PathVariable final Long roleId,
            @RequestBody final ReplaceRolePermissionsRequestDto request,
            @CurrentUserId final Long userId) {

        RolePermissionListResponseDto updated =
                rolePermissionCommandService.replacePermissions(roleId, request.getPermissionIds(), userId);

        return ResponseEntity.ok(updated);
    }

    // 역할-권한 삭제
    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    @PreAuthorize("hasAuthority('IAM_ROLE_PERMISSION_REMOVE')")
    public ResponseEntity<Void> deletePermission(
            @PathVariable final Long roleId,
            @PathVariable final Long permissionId,
            @CurrentUserId final Long deleterId) {

        rolePermissionCommandService.removePermission(roleId, permissionId, deleterId);
        return ResponseEntity.ok().build();
    }

    // -------------------------------------------
    // Query
    // -------------------------------------------
    // Role Query
    // -----------------------

    // 역할 상세 조회
    @GetMapping("/{roleId}")
    @PreAuthorize("hasAuthority('IAM_ROLE_READ')")
    public ResponseEntity<RoleResponseDto> getRole(@PathVariable final Long roleId) {
        return ResponseEntity.ok(roleQueryService.getRole(roleId));
    }

    // 역할 목록 조회
    @GetMapping
    @PreAuthorize("hasAuthority('IAM_ROLE_READ')")
    public ResponseEntity<RoleListResponseDto> getRoleList() {
        return ResponseEntity.ok(roleQueryService.getRoles());
    }

    // -----------------------
    // RolePermission Query
    // -----------------------

}
