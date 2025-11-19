package com.beyond.qiin.domain.iam.repository;

import com.beyond.qiin.domain.iam.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {

    // 사번
    Optional<User> findByUserNo(final String userNo);

    // 이메일
    Optional<User> findByEmail(final String email);

    // 사원명
    Boolean existsByUserName(final String userName);

    User findByUserName(final String userName);
}
