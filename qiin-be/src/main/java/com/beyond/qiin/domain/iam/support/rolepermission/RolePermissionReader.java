package com.beyond.qiin.domain.iam.support.rolepermission;

import com.beyond.qiin.domain.iam.entity.Permission;
import com.beyond.qiin.domain.iam.entity.Role;
import com.beyond.qiin.domain.iam.entity.RolePermission;
import com.beyond.qiin.domain.iam.repository.RolePermissionJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RolePermissionReader {

    private final RolePermissionJpaRepository rolePermissionJpaRepository;

    // 단건 조회 필요 시
    public RolePermission findById(final Long id) {
        return rolePermissionJpaRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("RolePermission not found"));
    }

    // Permission이 하나라도 참조 중인지 true
    public boolean existsByPermission(final Permission permission) {
        return rolePermissionJpaRepository.existsByPermission(permission);
    }

    // Role이 특정 Permission을 가지고 있는지
    public boolean existsByRoleAndPermission(final Role role, final Permission permission) {
        return rolePermissionJpaRepository.existsByRoleAndPermission(role, permission);
    }

    // Role의 Permission 전체 조회
    public List<RolePermission> findAllByRole(final Role role) {
        return rolePermissionJpaRepository.findAllByRole(role);
    }

    // Permission을 기준으로 RolePermission 조회
    public List<RolePermission> findByPermission(final Permission permission) {
        return rolePermissionJpaRepository.findByPermission(permission);
    }
}
