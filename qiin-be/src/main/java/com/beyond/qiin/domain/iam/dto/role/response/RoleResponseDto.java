package com.beyond.qiin.domain.iam.dto.role.response;

import com.beyond.qiin.domain.iam.dto.role_permission.response.RolePermissionResponseDto;
import com.beyond.qiin.domain.iam.entity.Role;
import com.beyond.qiin.infra.redis.iam.role.RoleReadModel;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RoleResponseDto {

    private final Long roleId;
    private final String roleDescription;
    private final String roleName;

    private final int userCount;
    private final List<RolePermissionResponseDto> permissions;

    public static RoleResponseDto fromEntity(final Role role) {

        List<RolePermissionResponseDto> perms = role.getRolePermissions().stream()
                .map(RolePermissionResponseDto::fromEntity)
                .toList();

        int userCount = role.getUserRoles().size();

        return RoleResponseDto.builder()
                .roleId(role.getId())
                .roleName(role.getRoleName())
                .roleDescription(role.getRoleDescription())
                .permissions(perms)
                .userCount(userCount)
                .build();
    }

    // redis
    public static RoleResponseDto fromReadModel(final RoleReadModel model) {

        List<RoleReadModel.PermissionItem> items = model.getPermissions() == null ? List.of() : model.getPermissions();

        List<RolePermissionResponseDto> perms =
                items.stream().map(RolePermissionResponseDto::fromRedisItem).toList();

        return RoleResponseDto.builder()
                .roleId(model.getRoleId())
                .roleName(model.getRoleName())
                .roleDescription(model.getRoleDescription())
                .permissions(perms)
                .userCount(model.getUserCount() == null ? 0 : model.getUserCount())
                .build();
    }
}
