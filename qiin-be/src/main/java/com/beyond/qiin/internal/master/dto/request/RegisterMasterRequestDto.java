package com.beyond.qiin.internal.master.dto.request;

import com.beyond.qiin.domain.iam.entity.User;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegisterMasterRequestDto {
    private Long dptId;
    private String userNo;
    private String userName;
    private String email;
    private Instant hireDate;

    public User toEntity(final String encryptedPassword) {
        return User.builder()
                .dptId(this.dptId)
                .userNo(this.userNo)
                .userName(this.userName)
                .email(this.email)
                .password(encryptedPassword)
                .passwordExpired(true) // 최초 로그인 시 비번 변경 필요
                .hireDate(this.hireDate)
                .build();
    }
}
