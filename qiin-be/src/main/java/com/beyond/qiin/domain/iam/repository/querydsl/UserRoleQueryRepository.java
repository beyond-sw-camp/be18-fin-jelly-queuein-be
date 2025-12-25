package com.beyond.qiin.domain.iam.repository.querydsl;

import com.beyond.qiin.domain.iam.entity.UserRole;
import java.util.Optional;

public interface UserRoleQueryRepository {

    Optional<UserRole> findUserRoleWithPermissions(final Long userId);
}
