package com.beyond.qiin.domain.iam.support.permission;

import com.beyond.qiin.domain.iam.entity.Permission;
import com.beyond.qiin.domain.iam.exception.PermissionException;
import com.beyond.qiin.domain.iam.repository.PermissionJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PermissionReader {

    private final PermissionJpaRepository permissionJpaRepository;

    public Permission findById(final Long permissionId) {
        return permissionJpaRepository.findById(permissionId).orElseThrow(PermissionException::permissionNotFound);
    }

    public Permission findByName(final String name) {
        return permissionJpaRepository.findAll().stream()
                .filter(p -> p.getPermissionName().equals(name))
                .findFirst()
                .orElseThrow(PermissionException::permissionNotFound);
    }

    // 중복된 권한명 있는지
    public void validateDuplication(final String name) {
        if (permissionJpaRepository.existsByPermissionName(name)) {
            throw PermissionException.permissionAlreadyExists();
        }
    }

    public List<Permission> findAll() {
        return permissionJpaRepository.findAll();
    }
}
