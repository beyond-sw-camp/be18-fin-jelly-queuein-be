package com.beyond.qiin.domain.iam.support.user;

import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.entity.UserProfile;
import com.beyond.qiin.domain.iam.repository.UserProfileJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserProfileWriter {

    private final UserProfileJpaRepository userProfileJpaRepository;

    public UserProfile save(final UserProfile profile) {
        return userProfileJpaRepository.save(profile);
    }

    public void deleteByUser(final User user) {
        userProfileJpaRepository.deleteByUser(user);
    }
}
