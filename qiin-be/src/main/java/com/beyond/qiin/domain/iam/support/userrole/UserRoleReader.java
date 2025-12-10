package com.beyond.qiin.domain.iam.support.userrole;

import com.beyond.qiin.domain.iam.entity.UserRole;
import com.beyond.qiin.domain.iam.exception.RoleException;
import com.beyond.qiin.domain.iam.repository.UserRoleJpaRepository;
import com.beyond.qiin.internal.auth.dto.UserRoleContextDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRoleReader {

    private final UserRoleJpaRepository userRoleJpaRepository;

    // 사용자 → Role → Permission 리스트 조회(로그인 시 사용)
    public UserRoleContextDto readUserRoleContext(final Long userId) {

        UserRole ur =
                userRoleJpaRepository.findUserRoleWithPermissions(userId).orElseThrow(RoleException::roleNotFound);

        String roleName = ur.getRole().getRoleName();

        List<String> permissions = ur.getRole().getRolePermissions().stream()
                .map(rp -> rp.getPermission().getPermissionName())
                .toList();

        return UserRoleContextDto.of(roleName, permissions);
    }

    public boolean existsMaster() {
        return userRoleJpaRepository.existsByRole_RoleName("MASTER");
    }
}
