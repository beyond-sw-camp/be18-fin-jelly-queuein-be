package com.beyond.qiin.domain.iam.service.query;

import com.beyond.qiin.domain.iam.dto.user.response.DetailUserResponseDto;
import com.beyond.qiin.domain.iam.dto.user.response.UserResponseDto;
import java.util.List;

public interface UserQueryService {
    List<UserResponseDto> getUsers();

    DetailUserResponseDto getUser(final Long userId);
}
