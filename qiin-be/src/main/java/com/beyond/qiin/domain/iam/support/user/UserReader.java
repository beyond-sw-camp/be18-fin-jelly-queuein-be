package com.beyond.qiin.domain.iam.support.user;

import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.exception.UserException;
import com.beyond.qiin.domain.iam.repository.UserJpaRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserReader {

    private final UserJpaRepository userJpaRepository;

    // ------------------------------------------------
    // 사용자 단건 조회
    // ------------------------------------------------

    // pk기반 유저 조회
    public User findById(final Long userId) {
        return userJpaRepository.findById(userId).orElseThrow(UserException::userNotFound);
    }

    // 이메일 기반 유저 조회
    public User findByEmail(final String email) {
        return userJpaRepository.findByEmail(email).orElseThrow(UserException::userNotFound);
    }

    // 사번 기반 유저 조회
    public User findByUserNo(final String userNo) {
        return userJpaRepository.findByUserNo(userNo).orElseThrow(UserException::userNotFound);
    }

    // 입력받은 달 마지막 사번 조회
    public Optional<String> findLastUserNoByPrefix(final String prefix) {
        return userJpaRepository.findLastUserNoByPrefix(prefix);
    }

    // ------------------------------------------------
    // 사용자 여러건 조회
    // ------------------------------------------------

    // 여러 유저 조회
    public List<User> findAllByIds(final List<Long> userIds) {

        // 전체 조회
        List<User> users = userJpaRepository.findAllById(userIds);

        // 존재하지 않는 ID가 있는지 검증
        if (users.size() != userIds.size()) {
            throw UserException.userNotFound();
        }

        // 입력한 ID 순서 보존해서 반환
        Map<Long, User> map = users.stream().collect(Collectors.toMap(User::getId, u -> u));

        return userIds.stream().map(map::get).toList();
    }

    // 모든 사용자 ID가 존재하면 true, 하나라도 없으면 false 반환
    public boolean existsAll(final Iterable<Long> userIds) {
        for (Long id : userIds) {
            if (!userJpaRepository.existsById(id)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 모든 사용자 ID가 존재해야만 하고, 하나라도 존재하지 않으면 예외 발생
     * REVIEW: 채연님 stream 버전은 이거입니다.
     **/
    public void validateAllExist(final Iterable<Long> userIds) {
        StreamSupport.stream(userIds.spliterator(), false).forEach(id -> {
            if (!userJpaRepository.existsById(id)) {
                throw UserException.userNotFound();
            }
        });
    }
}
