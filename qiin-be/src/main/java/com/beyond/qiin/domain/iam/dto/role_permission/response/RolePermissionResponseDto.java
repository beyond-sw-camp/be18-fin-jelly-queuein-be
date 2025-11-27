package com.beyond.qiin.domain.iam.dto.role_permission.response;

import com.beyond.qiin.domain.iam.entity.RolePermission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RolePermissionResponseDto {

    private final Long rolePermissionId;
    private final Long roleId;
    private final Long permissionId;
    private final String permissionName;

    public static RolePermissionResponseDto fromEntity(final RolePermission entity) {
        return RolePermissionResponseDto.builder()
                .rolePermissionId(entity.getId())
                .roleId(entity.getRole().getId())
                .permissionId(entity.getPermission().getId())
                .permissionName(entity.getPermission().getPermissionName())
                .build();
    }
}
