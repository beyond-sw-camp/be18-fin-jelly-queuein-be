package com.beyond.qiin.internal.master.dto.response;

import com.beyond.qiin.domain.iam.entity.Role;
import com.beyond.qiin.domain.iam.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterMasterResponseDto { // 처음에 MASTER 발급 시 추후 명칭 변경
    private Long userId;
    private String email;
    private String role;
    private String tempPassword;

    public static RegisterMasterResponseDto fromEntity(final User user, final Role role, final String tempPassword) {
        return RegisterMasterResponseDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(role.getRoleName())
                .tempPassword(tempPassword)
                .build();
    }
}
