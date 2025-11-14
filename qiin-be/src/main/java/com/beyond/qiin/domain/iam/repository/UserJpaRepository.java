package com.beyond.qiin.domain.iam.repository;

import com.beyond.qiin.domain.iam.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {
    Boolean existsByUsername(final String userName);

    User findByUsername(final String userName);
}
