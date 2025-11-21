package com.beyond.qiin.domain.iam.service.query;

import com.beyond.qiin.domain.iam.dto.user.response.DetailUserResponseDto;
import com.beyond.qiin.domain.iam.dto.user.response.UserResponseDto;
import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.repository.UserJpaRepository;
import com.beyond.qiin.domain.iam.support.user.UserReader;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {
    private final UserJpaRepository userJpaRepository;
    private final UserReader userReader;

    // 사용자 전체 조회
    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getUsers() {
        return userJpaRepository.findAll().stream()
                .map(UserResponseDto::fromEntity)
                .toList();
    }

    // 사용자 1명 조회
    @Override
    @Transactional(readOnly = true)
    public DetailUserResponseDto getUser(final Long userId) {
        User user = userReader.findById(userId);
        return DetailUserResponseDto.fromEntity(user);
    }
}
