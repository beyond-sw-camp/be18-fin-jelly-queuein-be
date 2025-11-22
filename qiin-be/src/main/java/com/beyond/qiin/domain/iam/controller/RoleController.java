package com.beyond.qiin.domain.iam.controller;

import com.beyond.qiin.domain.iam.dto.role.request.CreateRoleRequestDto;
import com.beyond.qiin.domain.iam.dto.role.response.RoleResponseDto;
import com.beyond.qiin.domain.iam.service.command.RoleCommandService;
import com.beyond.qiin.domain.iam.service.query.RoleQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleCommandService roleCommandService;
    private final RoleQueryService roleQueryService;

    // 역할 추가
    @PostMapping
    @PreAuthorize("hasAuthority('MASTER')")
    public ResponseEntity<RoleResponseDto> createRole(@Valid @RequestBody final CreateRoleRequestDto request) {
        return ResponseEntity.ok(roleCommandService.createRole(request));
    }
}
