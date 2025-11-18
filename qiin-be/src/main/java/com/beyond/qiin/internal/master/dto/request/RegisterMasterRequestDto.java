package com.beyond.qiin.internal.master.dto.request;

import com.beyond.qiin.domain.iam.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegisterMasterRequestDto {

    private Long dptId;
    // TODO: 사번 생성 로직으로 처리하기 or MASTER 명시
    private String userNo;

    @NotBlank
    private String userName;

    @NotBlank
    private String email;

    @NotNull
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
