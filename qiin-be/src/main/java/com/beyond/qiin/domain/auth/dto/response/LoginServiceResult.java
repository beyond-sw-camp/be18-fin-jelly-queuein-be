package com.beyond.qiin.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginServiceResult {

    private final LoginResult loginResult;
    private final String refreshToken;
}
