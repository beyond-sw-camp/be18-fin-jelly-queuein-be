package com.beyond.qiin.internal.auth.dto;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 역할 + 권한 조회값 묶음
 * 서비스 레이어 내부에서만 사용하는 dto라 internal에 넣음
 **/
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRoleContextDto {

    private String role;
    private List<String> permissions;

    private UserRoleContextDto(final String role, final List<String> permissions) {
        this.role = role;
        this.permissions = permissions;
    }

    public static UserRoleContextDto of(final String role, final List<String> permissions) {
        return new UserRoleContextDto(role, permissions);
    }
}
