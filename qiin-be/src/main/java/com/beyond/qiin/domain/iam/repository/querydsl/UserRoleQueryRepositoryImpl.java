package com.beyond.qiin.domain.iam.repository.querydsl;

import static com.beyond.qiin.domain.iam.entity.QPermission.permission;
import static com.beyond.qiin.domain.iam.entity.QRole.role;
import static com.beyond.qiin.domain.iam.entity.QRolePermission.rolePermission;
import static com.beyond.qiin.domain.iam.entity.QUserRole.userRole;

import com.beyond.qiin.domain.iam.entity.UserRole;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRoleQueryRepositoryImpl implements UserRoleQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<UserRole> findUserRoleWithPermissions(final Long userId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(userRole)
                .join(userRole.role, role)
                .fetchJoin()
                .join(role.rolePermissions, rolePermission)
                .fetchJoin()
                .join(rolePermission.permission, permission)
                .fetchJoin()
                .where(userRole.user.id.eq(userId))
                .fetchOne());
    }
}
