package com.beyond.qiin.domain.iam.service.query;

import com.beyond.qiin.domain.iam.dto.user.response.DetailUserResponseDto;
import com.beyond.qiin.domain.iam.dto.user.response.ListUserResponseDto;
import java.util.List;

public interface UserQueryService {
    List<ListUserResponseDto> getUsers();

    DetailUserResponseDto getUser(final Long userId);
}
