package com.beyond.qiin.domain.iam.support.user;

import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserWriter {

    private final UserJpaRepository userJpaRepository;

    // user 저장
    public User save(final User user) {
        return userJpaRepository.save(user);
    }

    // 유저 삭제
    public void delete(final User user) {
        user.delete(user.getId());
        userJpaRepository.save(user);
    }
}
