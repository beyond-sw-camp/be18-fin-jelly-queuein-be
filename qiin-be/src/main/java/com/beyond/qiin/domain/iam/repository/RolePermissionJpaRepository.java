package com.beyond.qiin.domain.iam.repository;

import com.beyond.qiin.domain.iam.entity.Role;
import com.beyond.qiin.domain.iam.entity.RolePermission;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolePermissionJpaRepository extends JpaRepository<RolePermission, Long> {

    List<RolePermission> findByRole(final Role role);
}
