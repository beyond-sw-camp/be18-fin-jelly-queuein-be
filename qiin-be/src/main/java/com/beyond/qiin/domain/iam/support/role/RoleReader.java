package com.beyond.qiin.domain.iam.support.role;

import com.beyond.qiin.domain.iam.entity.Role;
import com.beyond.qiin.domain.iam.exception.RoleException;
import com.beyond.qiin.domain.iam.repository.RoleJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleReader {

    private final RoleJpaRepository roleJpaRepository;

    // 역할 단건 조회
    public Role findById(final Long roleId) {
        return roleJpaRepository.findById(roleId).orElseThrow(RoleException::roleNotFound);
    }

    // 역할명 단건 조회
    public Role findByRoleName(final String roleName) {
        return roleJpaRepository.findByRoleName(roleName).orElseThrow(RoleException::roleNotFound);
    }

    // 역할명 중복 체크
    /*
    public void validateNameDuplication(final String roleName) {
        if (roleJpaRepository.findByRoleName(roleName).isPresent()) {
            throw RoleException.roleAlreadyExists();
        }
    }
    */

    // 역할명 중복 체크 (소프트 딜리트)
    public void validateNameDuplication(final String roleName) {

        Role existed = roleJpaRepository.findIncludeDeletedByName(roleName);

        if (existed == null) {
            return; // 완전 신규
        }

        // soft delete 된 역할명
        if (existed.getDeletedAt() != null) {
            throw RoleException.roleAlreadyDeleted();
        }

        // 현재 사용 중인 역할명
        throw RoleException.roleAlreadyExists();
    }

    // 역할 전체 조회
    public List<Role> findAll() {
        return roleJpaRepository.findAll();
    }
}
