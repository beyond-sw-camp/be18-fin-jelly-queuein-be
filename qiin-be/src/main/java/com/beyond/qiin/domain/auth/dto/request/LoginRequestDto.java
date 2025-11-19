package com.beyond.qiin.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginRequestDto {

    // private String userNo;
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    // email 기반으로 로그인 시 검색 키 생성
    public String getLoginKey() {
        return this.email;
    }
}
