package com.beyond.qiin.security.login;

import lombok.Getter;

@Getter
public final class LoginRequestDto {
    private String email;
    private String password;
}
