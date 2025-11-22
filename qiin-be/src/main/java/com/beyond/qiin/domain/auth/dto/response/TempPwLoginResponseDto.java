package com.beyond.qiin.domain.auth.dto.response;

import com.beyond.qiin.domain.iam.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TempPwLoginResponseDto implements LoginResult {

    private final Long userId;
    private final String userNo;
    private final String userName;
    private final String email;
    private final Boolean mustChangePassword;
    private final String accessToken;

    public static TempPwLoginResponseDto fromEntity(final User user, final String accessToken) {
        return TempPwLoginResponseDto.builder()
                .userId(user.getId())
                .userNo(user.getUserNo())
                .userName(user.getUserName())
                .email(user.getEmail())
                .mustChangePassword(user.getPasswordExpired())
                .accessToken(accessToken)
                .build();
    }
}
