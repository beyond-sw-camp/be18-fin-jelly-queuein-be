package com.beyond.qiin.domain.iam.support.permission;

import com.beyond.qiin.domain.iam.entity.Permission;
import com.beyond.qiin.domain.iam.repository.PermissionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PermissionWriter {

    private final PermissionJpaRepository permissionJpaRepository;

    public Permission save(final Permission permission) {
        return permissionJpaRepository.save(permission);
    }
}
