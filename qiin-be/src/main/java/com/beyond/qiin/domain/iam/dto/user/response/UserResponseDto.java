package com.beyond.qiin.domain.iam.dto.user.response;

import com.beyond.qiin.domain.iam.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserResponseDto {

    private final Long userId;
    private final String userName;
    private final String email;
    private final Boolean passwordExpired;

    public static UserResponseDto fromEntity(final User user) {
        return UserResponseDto.builder()
                .userId(user.getId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .passwordExpired(user.getPasswordExpired())
                .build();
    }
}
