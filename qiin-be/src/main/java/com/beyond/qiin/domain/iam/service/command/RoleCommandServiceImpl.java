package com.beyond.qiin.domain.iam.service.command;

import static com.beyond.qiin.domain.iam.constants.SystemRole.SYSTEM_ROLES;

import com.beyond.qiin.domain.iam.dto.role.request.CreateRoleRequestDto;
import com.beyond.qiin.domain.iam.dto.role.request.UpdateRoleRequestDto;
import com.beyond.qiin.domain.iam.dto.role.response.RoleResponseDto;
import com.beyond.qiin.domain.iam.entity.Role;
import com.beyond.qiin.domain.iam.exception.RoleException;
import com.beyond.qiin.domain.iam.support.role.RoleReader;
import com.beyond.qiin.domain.iam.support.role.RoleWriter;
import com.beyond.qiin.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleCommandServiceImpl implements RoleCommandService {

    private final RoleReader roleReader;
    private final RoleWriter roleWriter;

    // 역할 생성
    @Override
    @Transactional
    public RoleResponseDto createRole(final CreateRoleRequestDto request) {

        roleReader.validateNameDuplication(request.getRoleName());

        Role role = Role.builder()
                .roleName(request.getRoleName())
                .roleDescription(request.getRoleDescription())
                .build();

        Role savedRole = roleWriter.save(role);

        return RoleResponseDto.fromEntity(savedRole);
    }

    // 역할 수정
    @Override
    @Transactional
    public RoleResponseDto updateRole(final Long roleId, final UpdateRoleRequestDto request) {

        Role role = roleReader.findById(roleId);

        validateNotMaster(role.getRoleName());
        validateNotSystemRole(role.getRoleName());

        role.update(request.getRoleName(), request.getRoleDescription());

        Role updated = roleWriter.save(role);

        return RoleResponseDto.fromEntity(updated);
    }

    // 역할 삭제
    @Override
    @Transactional
    public void deleteRole(final Long roleId) {

        Role role = roleReader.findById(roleId);

        validateNotMaster(role.getRoleName());
        validateNotSystemRole(role.getRoleName());

        // TODO: Resolver로 변경
        role.softDelete(SecurityUtils.getCurrentUserId());
    }

    // -------------------------------
    // 헬퍼 메서드
    // -------------------------------

    // MASTER 역할 보호
    private void validateNotMaster(final String roleName) {
        if ("MASTER".equals(roleName)) {
            throw RoleException.roleCannotDeleteMaster();
        }
    }

    // 시스템 기본 역할 보호
    private void validateNotSystemRole(final String roleName) {
        if (SYSTEM_ROLES.contains(roleName)) {
            throw RoleException.systemRoleProtected();
        }
    }
}
