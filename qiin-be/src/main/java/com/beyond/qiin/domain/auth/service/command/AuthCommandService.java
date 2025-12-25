package com.beyond.qiin.domain.auth.service.command;

import com.beyond.qiin.domain.auth.dto.request.LoginRequestDto;
import com.beyond.qiin.domain.auth.dto.response.LoginServiceResult;

public interface AuthCommandService {

    LoginServiceResult login(final LoginRequestDto request);

    void logout(final String accessToken);

    LoginServiceResult refresh(final String refreshToken);
}
