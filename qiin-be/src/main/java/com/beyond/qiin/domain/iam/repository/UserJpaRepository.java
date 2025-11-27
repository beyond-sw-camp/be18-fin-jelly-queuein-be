package com.beyond.qiin.domain.iam.repository;

import com.beyond.qiin.domain.iam.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    @Query(
            """
    SELECT u.userNo
    FROM User u
    WHERE u.userNo LIKE CONCAT(:prefix, '%')
    ORDER BY u.userNo DESC
    LIMIT 1
""")
    Optional<String> findLastUserNoByPrefix(final String prefix);
}
