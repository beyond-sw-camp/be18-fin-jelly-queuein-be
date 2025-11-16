package com.beyond.qiin.domain.auth.dto.response;

import com.beyond.qiin.domain.iam.entity.Role;
import com.beyond.qiin.domain.iam.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignupResponseDto { // 처음에 MASTER 발급 시 추후 명칭 변경
    private Long userId;
    private String email;
    private String role; // MASTER 고정

    public static SignupResponseDto fromEntity(final User user, final Role role) {
        return SignupResponseDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(role.getRoleName())
                .build();
    }
}
