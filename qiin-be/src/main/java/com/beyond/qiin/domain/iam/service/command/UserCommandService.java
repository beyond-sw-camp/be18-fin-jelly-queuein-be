package com.beyond.qiin.domain.iam.service.command;

import com.beyond.qiin.domain.iam.dto.user.request.ChangePwRequestDto;
import com.beyond.qiin.domain.iam.dto.user.request.CreateUserRequestDto;
import com.beyond.qiin.domain.iam.dto.user.request.UpdateMyInfoRequestDto;
import com.beyond.qiin.domain.iam.dto.user.request.UpdateUserByAdminRequestDto;
import com.beyond.qiin.domain.iam.dto.user.response.CreateUserResponseDto;

public interface UserCommandService {

    CreateUserResponseDto createUser(final CreateUserRequestDto request);

    void updateUserRole(final Long userId, final Long roleId, final Long updaterId);

    void updateUser(final Long userId, final UpdateUserByAdminRequestDto request);

    void updateMyInfo(final Long userId, final UpdateMyInfoRequestDto request);

    void changeTempPassword(final Long userId, final String newPassword);

    void changePassword(final Long userId, final ChangePwRequestDto request);

    void deleteUser(final Long userId, final Long deleterId);
}
