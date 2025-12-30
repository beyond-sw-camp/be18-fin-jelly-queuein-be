package com.beyond.qiin.domain.iam.repository;

import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.entity.UserProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileJpaRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByUser(final User user);

    void deleteByUser(User user);
}
