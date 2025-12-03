package com.beyond.qiin.domain.iam.repository.querydsl;

import com.beyond.qiin.domain.iam.dto.user.request.search_condition.GetUsersSearchCondition;
import com.beyond.qiin.domain.iam.dto.user.response.raw.RawUserListResponseDto;
import com.beyond.qiin.domain.iam.dto.user.response.raw.RawUserLookupDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserQueryRepository {

    Page<RawUserListResponseDto> search(final GetUsersSearchCondition condition, final Pageable pageable);

    List<RawUserLookupDto> lookup(final String keyword);
}
