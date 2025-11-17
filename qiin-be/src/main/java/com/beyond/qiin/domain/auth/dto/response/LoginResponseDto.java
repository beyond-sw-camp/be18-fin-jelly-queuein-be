package com.beyond.qiin.domain.auth.dto.response;

import com.beyond.qiin.domain.iam.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDto {

    private Long userId;
    private String userName;
    private String email;
    private String role;
    private Boolean passwordExpired;
    private String accessToken;
    private String refreshToken;

    public static LoginResponseDto of(
            final User user, final String role, final String accessToken, final String refreshToken) {
        return LoginResponseDto.builder()
                .userId(user.getId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .role(role)
                .passwordExpired(user.getPasswordExpired())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
