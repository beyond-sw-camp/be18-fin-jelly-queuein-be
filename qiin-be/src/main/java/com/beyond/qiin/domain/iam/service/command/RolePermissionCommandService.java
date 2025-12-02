package com.beyond.qiin.domain.iam.service.command;

import com.beyond.qiin.domain.iam.dto.role_permission.response.RolePermissionListResponseDto;
import com.beyond.qiin.domain.iam.dto.role_permission.response.RolePermissionResponseDto;
import java.util.List;

public interface RolePermissionCommandService {

    RolePermissionResponseDto addPermission(final Long roleId, final Long permissionId);

    RolePermissionListResponseDto addPermissions(final Long roleId, final List<Long> permissionIds);

    RolePermissionListResponseDto replacePermissions(
            final Long roleId, final List<Long> permissionIds, final Long userId);

    RolePermissionListResponseDto removePermission(final Long roleId, final Long permissionId, final Long deleterId);
}
