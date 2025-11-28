package com.beyond.qiin.domain.iam.support.userrole;

import com.beyond.qiin.domain.iam.exception.RoleException;
import com.beyond.qiin.domain.iam.repository.UserRoleJpaRepository;
import com.beyond.qiin.domain.iam.support.rolepermission.RolePermissionReader;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRoleReader {

    private final UserRoleJpaRepository userRoleJpaRepository;
    private final RolePermissionReader rolePermissionReader;

    public String findRoleNameByUserId(final Long userId) {
        return userRoleJpaRepository
                .findTopByUser_Id(userId)
                .map(ur -> ur.getRole().getRoleName())
                .orElseThrow(RoleException::roleNotFound);
    }

    // 사용자 → Role → Permission 리스트 조회
    public List<String> findPermissionsByUserId(final Long userId) {
        return userRoleJpaRepository
                .findTopByUser_Id(userId)
                .map(ur -> rolePermissionReader.findAllByRole(ur.getRole()))
                .orElseThrow(RoleException::roleNotFound)
                .stream()
                .map(rp -> rp.getPermission().getPermissionName())
                .collect(Collectors.toList());
    }

    public boolean existsMaster() {
        return userRoleJpaRepository.existsByRole_RoleName("MASTER");
    }
}
