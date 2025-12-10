package com.beyond.qiin.domain.iam.repository;

import com.beyond.qiin.domain.iam.entity.UserRole;
import com.beyond.qiin.domain.iam.repository.querydsl.UserRoleQueryRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleJpaRepository extends JpaRepository<UserRole, Long>, UserRoleQueryRepository {

    // 기존 역할명 확인
    boolean existsByRole_RoleName(final String roleName);

    Optional<UserRole> findTopByUser_Id(final Long userId);
}
