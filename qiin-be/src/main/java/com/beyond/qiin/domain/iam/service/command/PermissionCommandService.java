package com.beyond.qiin.domain.iam.service.command;

import com.beyond.qiin.domain.iam.dto.permission.request.CreatePermissionRequestDto;
import com.beyond.qiin.domain.iam.dto.permission.request.UpdatePermissionRequestDto;
import com.beyond.qiin.domain.iam.dto.permission.response.PermissionResponseDto;

public interface PermissionCommandService {

    PermissionResponseDto createPermission(final CreatePermissionRequestDto request);

    PermissionResponseDto updatePermission(final Long permissionId, final UpdatePermissionRequestDto request);

    void deletePermission(final Long permissionId);
}
