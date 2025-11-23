package com.beyond.qiin.domain.iam.dto.role.response;

import com.beyond.qiin.domain.iam.entity.Role;
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

    // TODO: 해당 역할 몇명, 권한 목록 조회 가능
    //    private final int userCount;
    //    private final List<String> permissions;

    public static RoleResponseDto fromEntity(final Role role) {
        return RoleResponseDto.builder()
                .roleId(role.getId())
                .roleName(role.getRoleName())
                .roleDescription(role.getRoleDescription())
                .build();
    }
}
