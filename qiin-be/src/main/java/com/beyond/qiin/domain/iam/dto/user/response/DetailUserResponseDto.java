package com.beyond.qiin.domain.iam.dto.user.response;

import com.beyond.qiin.domain.iam.entity.Role;
import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.entity.UserProfile;
import com.beyond.qiin.domain.iam.entity.UserRole;
import com.beyond.qiin.domain.iam.exception.RoleException;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DetailUserResponseDto {
    private final Long userId;
    private final Long dptId;
    private final String userNo;
    private final String userName;
    private final String email;
    private final Boolean passwordExpired;
    private final Instant lastLoginAt;
    private final Instant hireDate;
    private final Instant retireDate;
    private final String phone;
    private final String birth;

    private final Long roleId;
    private final String roleName;

    private final String profileImageUrl;

    public static DetailUserResponseDto fromEntity(final User user, final UserProfile userProfile) {

        Role role = user.getUserRoles().stream()
                .findFirst()
                .map(UserRole::getRole)
                .orElseThrow(RoleException::roleNotFound);

        return DetailUserResponseDto.builder()
                .userId(user.getId())
                .dptId(user.getDptId())
                .userNo(user.getUserNo())
                .userName(user.getUserName())
                .email(user.getEmail())
                .passwordExpired(user.getPasswordExpired())
                .lastLoginAt(user.getLastLoginAt())
                .hireDate(user.getHireDate())
                .retireDate(user.getRetireDate())
                .phone(user.getPhone())
                .birth(user.getBirth())
                .roleId(role.getId())
                .roleName(role.getRoleName())
                .profileImageUrl(userProfile != null ? userProfile.getImageUrl() : null)
                .build();
    }
}
