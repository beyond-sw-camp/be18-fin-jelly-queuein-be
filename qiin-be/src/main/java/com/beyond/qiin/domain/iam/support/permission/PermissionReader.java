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

    // 권한 ID
    public Permission findById(final Long permissionId) {
        return permissionJpaRepository.findById(permissionId).orElseThrow(PermissionException::permissionNotFound);
    }

    // 권한 명
    public Permission findByName(final String name) {
        return permissionJpaRepository.findAll().stream()
                .filter(p -> p.getPermissionName().equals(name))
                .findFirst()
                .orElseThrow(PermissionException::permissionNotFound);
    }

    // 권한 목록 조회
    public List<Permission> findAll() {
        return permissionJpaRepository.findAll();
    }

    // 중복된 권한 명 있는지
    //    public void validateDuplication(final String name) {
    //        if (permissionJpaRepository.existsByPermissionName(name)) {
    //            throw PermissionException.permissionAlreadyExists();
    //        }
    //    }

    // 중복된 권한 예외처리(Softdelete Unique)
    public void validateDuplication(final String name) {

        Permission existed = permissionJpaRepository.findIncludeDeletedByName(name);

        // 완전히 새로운 권한명
        if (existed == null) return;

        // 삭제되었던 권한명
        if (existed.getDeletedAt() != null) {
            throw PermissionException.permissionAlreadyDeleted();
        }

        // 현재 존재하는 권한명
        throw PermissionException.permissionAlreadyExists();
    }
}
