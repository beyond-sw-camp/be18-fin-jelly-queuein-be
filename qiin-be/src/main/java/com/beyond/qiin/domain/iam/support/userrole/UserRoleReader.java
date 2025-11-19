package com.beyond.qiin.domain.iam.support.userrole;

import com.beyond.qiin.domain.iam.exception.RoleException;
import com.beyond.qiin.domain.iam.repository.UserRoleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRoleReader {

    private final UserRoleJpaRepository userRoleJpaRepository;

    public String findRoleNameByUserId(final Long userId) {
        return userRoleJpaRepository
                .findTopByUser_Id(userId)
                .map(ur -> ur.getRole().getRoleName())
                .orElseThrow(RoleException::roleNotFound);
    }

    public boolean existsMaster() {
        return userRoleJpaRepository.existsByRole_RoleName("MASTER");
    }
}
