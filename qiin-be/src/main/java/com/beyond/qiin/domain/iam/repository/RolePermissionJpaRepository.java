package com.beyond.qiin.domain.iam.repository;

import com.beyond.qiin.domain.iam.entity.Permission;
import com.beyond.qiin.domain.iam.entity.Role;
import com.beyond.qiin.domain.iam.entity.RolePermission;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolePermissionJpaRepository extends JpaRepository<RolePermission, Long> {

    // 역할 기준 관련권한들
    List<RolePermission> findAllByRole(final Role role);

    // 복합키 검증
    boolean existsByRoleAndPermission(final Role role, final Permission permission);

    // 권한 검색
    List<RolePermission> findByPermission(final Permission permission);

    // 기존의 권한 검증
    boolean existsByPermission(final Permission permission);
}
