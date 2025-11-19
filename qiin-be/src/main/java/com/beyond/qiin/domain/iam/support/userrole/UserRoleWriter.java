package com.beyond.qiin.domain.iam.support.userrole;

import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.entity.UserRole;
import com.beyond.qiin.domain.iam.repository.UserRoleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRoleWriter {

    private final UserRoleJpaRepository userRoleJpaRepository;

    public UserRole save(final UserRole userRole) {
        return userRoleJpaRepository.save(userRole);
    }

    public void deleteAllByUser(final User user) {
        user.getUserRoles().forEach(ur -> {
            ur.delete(ur.getId());
        });
        userRoleJpaRepository.saveAll(user.getUserRoles());
    }
}
