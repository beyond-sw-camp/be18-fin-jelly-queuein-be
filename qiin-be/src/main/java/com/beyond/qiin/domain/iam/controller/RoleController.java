package com.beyond.qiin.domain.iam.controller;

import com.beyond.qiin.domain.iam.dto.role.request.CreateRoleRequestDto;
import com.beyond.qiin.domain.iam.dto.role.request.UpdateRoleRequestDto;
import com.beyond.qiin.domain.iam.dto.role.response.RoleListResponseDto;
import com.beyond.qiin.domain.iam.dto.role.response.RoleResponseDto;
import com.beyond.qiin.domain.iam.service.command.RoleCommandService;
import com.beyond.qiin.domain.iam.service.query.RoleQueryService;
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
    private final RoleQueryService roleQueryService;

    // 역할 생성
    @PostMapping
    @PreAuthorize("hasAuthority('MASTER')")
    public ResponseEntity<RoleResponseDto> createRole(@Valid @RequestBody final CreateRoleRequestDto request) {
        return ResponseEntity.ok(roleCommandService.createRole(request));
    }

    // 역할 목록 조회
    @GetMapping
    @PreAuthorize("hasAnyAuthority('MASTER','ADMIN')")
    public ResponseEntity<RoleListResponseDto> getRoleList() {
        return ResponseEntity.ok(roleQueryService.getRoles());
    }

    // 역할 상세 조회
    @GetMapping("/{roleId}")
    @PreAuthorize("hasAnyAuthority('MASTER','ADMIN')")
    public ResponseEntity<RoleResponseDto> getRole(@PathVariable final Long roleId) {
        return ResponseEntity.ok(roleQueryService.getRole(roleId));
    }

    // REVIEW: PUT 유지하고 2개의 필드 무조건 보내게 하는거 프론트에서 검증하게 하며,
    // 500 안 던지게 하기
    // 역할 수정
    @PutMapping("/{roleId}")
    @PreAuthorize("hasAuthority('MASTER')")
    public ResponseEntity<RoleResponseDto> updateRole(
            @PathVariable final Long roleId, @Valid @RequestBody final UpdateRoleRequestDto request) {
        return ResponseEntity.ok(roleCommandService.updateRole(roleId, request));
    }

    // 역할 삭제
    @DeleteMapping("/{roleId}")
    @PreAuthorize("hasAuthority('MASTER')")
    public ResponseEntity<Void> deleteRole(@PathVariable final Long roleId) {
        roleCommandService.deleteRole(roleId);
        return ResponseEntity.noContent().build();
    }
}
