package com.beyond.qiin.domain.iam.dto.role_permission.response;

import com.beyond.qiin.domain.iam.entity.RolePermission;
import com.beyond.qiin.infra.redis.iam.role.RoleReadModel;
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
    private final String permissionDescription;

    public static RolePermissionResponseDto fromEntity(final RolePermission entity) {
        return RolePermissionResponseDto.builder()
                .rolePermissionId(entity.getId())
                .roleId(entity.getRole().getId())
                .permissionId(entity.getPermission().getId())
                .permissionName(entity.getPermission().getPermissionName())
                .permissionDescription(entity.getPermission().getPermissionDescription())
                .build();
    }

    public static RolePermissionResponseDto fromRedisItem(final RoleReadModel.PermissionItem p) {
        return RolePermissionResponseDto.builder()
                .rolePermissionId(null)
                .roleId(null)
                .permissionId(p.getPermissionId())
                .permissionName(p.getPermissionName())
                .permissionDescription(p.getPermissionDescription())
                .build();
    }
}
