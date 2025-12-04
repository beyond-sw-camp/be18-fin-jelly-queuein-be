package com.beyond.qiin.domain.iam.support.user;

import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.exception.UserException;
import com.beyond.qiin.domain.iam.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserWriter {

    private final UserJpaRepository userJpaRepository;

    // user 저장

    public User save(final User user) {
        try {
            return userJpaRepository.save(user);
        } catch (DataIntegrityViolationException e) {

            // MariaDB Duplicate Key 에러 메시지 매핑
            final String msg = e.getMostSpecificCause().getMessage();

            if (msg.contains("uk_user_no_active")) {
                throw UserException.userAlreadyExists();
            }
            if (msg.contains("uk_user_email_active")) {
                throw UserException.userAlreadyExists();
            }

            // 예상 못한 DB 제약이면 그대로 던짐
            throw e;
        }
    }

    // 유저 삭제
    public void delete(final User user) {
        user.delete(user.getId());
        save(user);
    }
}
