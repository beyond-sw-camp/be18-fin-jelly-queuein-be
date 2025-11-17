package com.beyond.qiin.domain.iam.service.command;

import com.beyond.qiin.domain.iam.dto.user.request.ChangePasswordRequestDto;
import com.beyond.qiin.domain.iam.dto.user.request.CreateUserRequestDto;
import com.beyond.qiin.domain.iam.dto.user.request.UpdateUserRequestDto;

public interface UserCommandService {

    void createUser(final CreateUserRequestDto request);

    void updateUser(final Long userId, final UpdateUserRequestDto request);

    void changePassword(final Long userId, final ChangePasswordRequestDto request);

    void deleteUser(final Long userId);
}
