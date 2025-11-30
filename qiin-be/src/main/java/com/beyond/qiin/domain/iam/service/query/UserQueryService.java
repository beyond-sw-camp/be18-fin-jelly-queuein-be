package com.beyond.qiin.domain.iam.service.query;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.iam.dto.user.request.search_condition.GetUsersSearchCondition;
import com.beyond.qiin.domain.iam.dto.user.response.DetailUserResponseDto;
import com.beyond.qiin.domain.iam.dto.user.response.UserLookupResponseDto;
import com.beyond.qiin.domain.iam.dto.user.response.raw.RawUserListResponseDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface UserQueryService {
    PageResponseDto<RawUserListResponseDto> searchUsers(GetUsersSearchCondition condition, Pageable pageable);

    List<UserLookupResponseDto> lookupUsers(final String keyword);

    DetailUserResponseDto getUser(final Long userId);
}
