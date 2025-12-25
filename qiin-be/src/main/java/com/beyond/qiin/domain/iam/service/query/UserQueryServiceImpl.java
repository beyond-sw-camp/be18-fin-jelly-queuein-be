package com.beyond.qiin.domain.iam.service.query;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.iam.dto.user.request.search_condition.GetUsersSearchCondition;
import com.beyond.qiin.domain.iam.dto.user.response.DetailUserResponseDto;
import com.beyond.qiin.domain.iam.dto.user.response.UserLookupResponseDto;
import com.beyond.qiin.domain.iam.dto.user.response.raw.RawUserListResponseDto;
import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.entity.UserProfile;
import com.beyond.qiin.domain.iam.repository.querydsl.UserQueryRepository;
import com.beyond.qiin.domain.iam.support.user.UserProfileReader;
import com.beyond.qiin.domain.iam.support.user.UserReader;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {

    private final UserReader userReader;
    private final UserProfileReader userProfileReader;
    private final UserQueryRepository userQueryRepository;

    // 사용자 전체 조회
    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<RawUserListResponseDto> searchUsers(
            final GetUsersSearchCondition condition, final Pageable pageable) {
        Page<RawUserListResponseDto> page = userQueryRepository.search(condition, pageable);
        return PageResponseDto.from(page);
    }

    // 참여자용 리스트 조회
    @Override
    @Transactional(readOnly = true)
    public List<UserLookupResponseDto> lookupUsers(final String keyword) {

        if (keyword == null || keyword.trim().isBlank()) {
            return List.of();
        }

        String normalized = keyword.trim();

        return userQueryRepository.lookup(normalized).stream()
                .map(UserLookupResponseDto::from)
                .toList();
    }

    // 사용자 1명 조회
    @Override
    @Transactional(readOnly = true)
    public DetailUserResponseDto getUser(final Long userId) {
        User user = userReader.findById(userId);
        UserProfile profile = userProfileReader.findByUser(user).orElse(null);
        return DetailUserResponseDto.fromEntity(user, profile);
    }
}
