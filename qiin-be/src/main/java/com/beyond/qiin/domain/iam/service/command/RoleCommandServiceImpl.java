package com.beyond.qiin.domain.iam.service.command;

import static com.beyond.qiin.domain.iam.constants.SystemRole.SYSTEM_ROLES;

import com.beyond.qiin.domain.iam.dto.role.request.CreateRoleRequestDto;
import com.beyond.qiin.domain.iam.dto.role.request.UpdateRoleRequestDto;
import com.beyond.qiin.domain.iam.dto.role.response.RoleResponseDto;
import com.beyond.qiin.domain.iam.entity.Role;
import com.beyond.qiin.domain.iam.exception.RoleException;
import com.beyond.qiin.domain.iam.support.role.RoleReader;
import com.beyond.qiin.domain.iam.support.role.RoleWriter;
import com.beyond.qiin.domain.iam.support.rolepermission.RolePermissionReader;
import com.beyond.qiin.domain.iam.support.rolepermission.RolePermissionWriter;
import com.beyond.qiin.infra.redis.iam.role.RoleProjectionHandler;
import com.beyond.qiin.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleCommandServiceImpl implements RoleCommandService {

    private final RoleReader roleReader;
    private final RoleWriter roleWriter;
    private final RoleProjectionHandler projectionHandler;

    private final RolePermissionReader rolePermissionReader;
    private final RolePermissionWriter rolePermissionWriter;

    // 역할 생성
    @Override
    @Transactional
    public RoleResponseDto createRole(final CreateRoleRequestDto request) {

        roleReader.validateNameDuplication(request.getRoleName());

        Role role = Role.create(request);

        Role savedRole = roleWriter.save(role);

        // redis projection
        projectionHandler.onRoleCreated(savedRole);

        return RoleResponseDto.fromEntity(savedRole);
    }

    // 역할 수정
    @Override
    @Transactional
    public RoleResponseDto updateRole(final Long roleId, final UpdateRoleRequestDto request) {

        Role role = roleReader.findById(roleId);

        validateNotMaster(role.getRoleName());
        validateNotSystemRole(role.getRoleName());

        Role updated = updateAndSave(role, request);

        // redis projection
        projectionHandler.onRoleUpdated(updated);

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

        // redis projection
        projectionHandler.onRoleDeleted(role);
    }

    // -------------------------------
    // 헬퍼 메서드
    // -------------------------------

    // update, save 캡슐화
    private Role updateAndSave(final Role role, final UpdateRoleRequestDto request) {
        role.update(request.getRoleName(), request.getRoleDescription());
        return roleWriter.save(role);
    }

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
