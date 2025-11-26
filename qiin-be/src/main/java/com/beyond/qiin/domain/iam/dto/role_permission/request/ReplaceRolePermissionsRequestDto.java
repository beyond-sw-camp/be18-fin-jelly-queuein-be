package com.beyond.qiin.domain.iam.dto.role_permission.request;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReplaceRolePermissionsRequestDto {
    private List<Long> permissionIds;
}
