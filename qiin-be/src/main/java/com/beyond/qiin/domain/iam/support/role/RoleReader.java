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

    public Role findById(Long roleId) {
        return roleJpaRepository.findById(roleId).orElseThrow(RoleException::roleNotFound);
    }

    public Role findByRoleName(final String roleName) {
        return roleJpaRepository.findByRoleName(roleName).orElseThrow(RoleException::roleNotFound);
    }

    public List<Role> findAll() {
        return roleJpaRepository.findAll();
    }

    public void validateNameDuplication(String roleName) {
        if (roleJpaRepository.findByRoleName(roleName).isPresent()) {
            throw RoleException.roleAlreadyExists();
        }
    }
}
