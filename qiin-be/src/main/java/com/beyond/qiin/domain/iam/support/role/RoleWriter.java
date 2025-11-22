package com.beyond.qiin.domain.iam.support.role;

import com.beyond.qiin.domain.iam.entity.Role;
import com.beyond.qiin.domain.iam.repository.RoleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleWriter {

    private final RoleJpaRepository roleJpaRepository;

    public Role save(final Role role) {
        return roleJpaRepository.save(role);
    }
}
