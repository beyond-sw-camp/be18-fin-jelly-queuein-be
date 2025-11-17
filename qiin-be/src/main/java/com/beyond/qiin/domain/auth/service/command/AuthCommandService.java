package com.beyond.qiin.domain.auth.service.command;

import com.beyond.qiin.domain.auth.dto.request.LoginRequestDto;
import com.beyond.qiin.domain.auth.dto.response.LoginResponseDto;

public interface AuthCommandService {

    LoginResponseDto login(final LoginRequestDto request);
}
