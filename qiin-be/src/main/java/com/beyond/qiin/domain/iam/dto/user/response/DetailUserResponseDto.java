package com.beyond.qiin.domain.iam.dto.user.response;

import com.beyond.qiin.domain.iam.entity.User;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DetailUserResponseDto {
    private Long userId;
    private Long dptId;
    private String userNo;
    private String userName;
    private String email;
    private Boolean passwordExpired;
    private Instant lastLoginAt;
    private Instant hireDate;
    private Instant retireDate;

    public static DetailUserResponseDto fromEntity(final User user) {
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
                .build();
    }
}
