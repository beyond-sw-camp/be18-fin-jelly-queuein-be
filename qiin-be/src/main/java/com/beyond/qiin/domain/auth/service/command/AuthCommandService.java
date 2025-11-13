package com.beyond.qiin.domain.auth.service.command;

import com.beyond.qiin.domain.auth.dto.request.SignupRequestDto;
import com.beyond.qiin.domain.auth.dto.response.SignupResponseDto;

public interface AuthCommandService {
    SignupResponseDto createMaster(final SignupRequestDto request);
}
