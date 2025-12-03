package com.beyond.qiin.domain.iam.service.command;

import com.beyond.qiin.domain.iam.dto.user.request.ChangePwRequestDto;
import com.beyond.qiin.domain.iam.dto.user.request.CreateUserRequestDto;
import com.beyond.qiin.domain.iam.dto.user.request.UpdateUserRequestDto;
import com.beyond.qiin.domain.iam.dto.user.response.CreateUserResponseDto;

public interface UserCommandService {

    CreateUserResponseDto createUser(final CreateUserRequestDto request);

    void updateUser(final Long userId, final UpdateUserRequestDto request);

    void changeTempPassword(final Long userId, final String newPassword);

    void changePassword(final Long userId, final ChangePwRequestDto request);

    void deleteUser(final Long userId, final Long deleterId);
}
