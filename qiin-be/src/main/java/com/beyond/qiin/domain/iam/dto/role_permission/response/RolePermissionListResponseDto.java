package com.beyond.qiin.domain.iam.dto.role_permission.response;

import com.beyond.qiin.domain.iam.entity.RolePermission;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RolePermissionListResponseDto {

    private final Long roleId;
    private final List<RolePermissionResponseDto> permissions;

    public static RolePermissionListResponseDto fromEntities(final Long roleId, final List<RolePermission> entities) {
        return RolePermissionListResponseDto.builder()
                .roleId(roleId)
                .permissions(entities.stream()
                        .map(RolePermissionResponseDto::fromEntity)
                        .toList())
                .build();
    }
}
