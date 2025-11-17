package com.beyond.qiin.domain.auth.dto.response;

import com.beyond.qiin.domain.iam.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TempPwLoginResponseDto implements LoginResult {

    private Long userId;
    private String userNo;
    private String userName;
    private String email;
    private Boolean mustChangePassword;
    private String accessToken;

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
