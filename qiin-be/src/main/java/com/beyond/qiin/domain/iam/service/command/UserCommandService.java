package com.beyond.qiin.domain.iam.service.command;

import com.beyond.qiin.domain.iam.dto.user.request.ChangePasswordRequestDto;

public interface UserCommandService {

    void changePassword(final Long userId, final ChangePasswordRequestDto request);
}
