package com.beyond.qiin.domain.iam.service.command;

import com.beyond.qiin.domain.iam.dto.role.request.CreateRoleRequestDto;
import com.beyond.qiin.domain.iam.dto.role.response.RoleResponseDto;

public interface RoleCommandService {

    RoleResponseDto createRole(final CreateRoleRequestDto request);
}
