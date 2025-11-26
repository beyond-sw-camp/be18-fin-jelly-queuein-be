package com.beyond.qiin.domain.iam.support.rolepermission;

import com.beyond.qiin.domain.iam.entity.RolePermission;
import com.beyond.qiin.domain.iam.repository.RolePermissionJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RolePermissionWriter {

    private final RolePermissionJpaRepository rolePermissionJpaRepository;

    public RolePermission save(final RolePermission rolePermission) {
        return rolePermissionJpaRepository.save(rolePermission);
    }

    // 다중 권한
    public List<RolePermission> saveAll(List<RolePermission> rolePermissions) {
        return rolePermissionJpaRepository.saveAll(rolePermissions); // batch insert
    }

    public void delete(final RolePermission rolePermission) {
        rolePermissionJpaRepository.delete(rolePermission);
    }

    public void softDelete(final RolePermission rolePermission, final Long deleterId) {
        rolePermission.delete(deleterId);
        rolePermissionJpaRepository.save(rolePermission);
    }
}
