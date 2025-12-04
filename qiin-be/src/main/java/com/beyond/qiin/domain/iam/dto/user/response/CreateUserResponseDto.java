package com.beyond.qiin.domain.iam.dto.user.response;

import com.beyond.qiin.domain.iam.entity.User;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CreateUserResponseDto {
    private final Long userId;
    private final Long dptId;
    private final String userNo;
    private final String userName;
    private final String email;
    private final Boolean passwordExpired;
    private final Instant lastLoginAt;
    private final Instant hireDate;
    private final Instant retireDate;

    public static CreateUserResponseDto fromEntity(final User user) {
        return CreateUserResponseDto.builder()
                .userId(user.getId())
                .dptId(user.getDptId())
                .userNo(user.getUserNo())
                .userName(user.getUserName())
                .email(user.getEmail())
                .passwordExpired(user.getPasswordExpired())
                .lastLoginAt(user.getLastLoginAt())
                .hireDate(user.getHireDate())
                .retireDate(user.getRetireDate())
                .build();
    }
}
