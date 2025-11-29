package com.beyond.qiin.domain.iam.service.query;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.iam.dto.user.request.search_condition.GetUsersSearchCondition;
import com.beyond.qiin.domain.iam.dto.user.response.DetailUserResponseDto;
import com.beyond.qiin.domain.iam.dto.user.response.raw.RawUserListResponseDto;
import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.repository.querydsl.UserQueryRepository;
import com.beyond.qiin.domain.iam.support.user.UserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {

    private final UserQueryRepository userQueryRepository;
    private final UserReader userReader;

    // 사용자 전체 조회
    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<RawUserListResponseDto> searchUsers(
            final GetUsersSearchCondition condition, final Pageable pageable) {

        var page = userQueryRepository.search(condition, pageable);
        return PageResponseDto.from(page);
    }

    // 사용자 1명 조회
    @Override
    @Transactional(readOnly = true)
    public DetailUserResponseDto getUser(final Long userId) {
        User user = userReader.findById(userId);
        return DetailUserResponseDto.fromEntity(user);
    }
}
