package com.beyond.qiin.domain.iam.service.query;

import com.beyond.qiin.domain.iam.dto.permission.response.PermissionListResponseDto;
import com.beyond.qiin.domain.iam.dto.permission.response.PermissionResponseDto;

public interface PermissionQueryService {

    PermissionResponseDto getPermission(final Long permissionId);

    PermissionListResponseDto getPermissions();
}
