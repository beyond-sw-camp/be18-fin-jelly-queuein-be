package com.beyond.qiin.domain.iam.service.command;

import com.beyond.qiin.domain.iam.dto.role.request.CreateRoleRequestDto;
import com.beyond.qiin.domain.iam.dto.role.request.UpdateRoleRequestDto;
import com.beyond.qiin.domain.iam.dto.role.response.RoleResponseDto;

public interface RoleCommandService {

    // 역할 생성
    RoleResponseDto createRole(final CreateRoleRequestDto request);

    // 역할 수정
    RoleResponseDto updateRole(final Long roleId, final UpdateRoleRequestDto request);

    // 역할 삭제
    void deleteRole(final Long roleId);
}
