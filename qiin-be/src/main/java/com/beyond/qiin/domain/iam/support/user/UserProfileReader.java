package com.beyond.qiin.domain.iam.support.user;

import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.entity.UserProfile;
import com.beyond.qiin.domain.iam.repository.UserProfileJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserProfileReader {

    private final UserProfileJpaRepository userProfileJpaRepository;

    public Optional<UserProfile> findByUser(final User user) {
        return userProfileJpaRepository.findByUser(user);
    }
}
