package com.beyond.qiin.domain.iam.repository;

import com.beyond.qiin.domain.iam.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    List<RolePermission> findByRole(final Role role);
}
