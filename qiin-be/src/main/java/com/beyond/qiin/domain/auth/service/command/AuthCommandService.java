package com.beyond.qiin.domain.auth.service.command;

import com.beyond.qiin.domain.auth.dto.request.LoginRequestDto;
import com.beyond.qiin.domain.auth.dto.response.LoginResult;

public interface AuthCommandService {

    LoginResult login(final LoginRequestDto request);

    void logout(final String accessToken);

    LoginResult refresh(final String refreshToken);
}
