package com.beyond.qiin.domain.iam.service.command;

import com.beyond.qiin.domain.iam.dto.role_permission.response.RolePermissionListResponseDto;
import com.beyond.qiin.domain.iam.dto.role_permission.response.RolePermissionResponseDto;
import com.beyond.qiin.domain.iam.entity.Permission;
import com.beyond.qiin.domain.iam.entity.Role;
import com.beyond.qiin.domain.iam.entity.RolePermission;
import com.beyond.qiin.domain.iam.exception.PermissionException;
import com.beyond.qiin.domain.iam.support.permission.PermissionReader;
import com.beyond.qiin.domain.iam.support.role.RoleReader;
import com.beyond.qiin.domain.iam.support.rolepermission.RolePermissionReader;
import com.beyond.qiin.domain.iam.support.rolepermission.RolePermissionWriter;
import com.beyond.qiin.infra.redis.iam.role.RoleProjectionHandler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RolePermissionCommandServiceImpl implements RolePermissionCommandService {

    private final RoleReader roleReader;
    private final PermissionReader permissionReader;

    private final RolePermissionReader rolePermissionReader;
    private final RolePermissionWriter rolePermissionWriter;

    private final RoleProjectionHandler projectionHandler;

    @Override
    @Transactional
    public RolePermissionResponseDto addPermission(final Long roleId, final Long permissionId) {

        Role role = roleReader.findById(roleId);
        Permission permission = permissionReader.findById(permissionId);

        if (rolePermissionReader.existsByRoleAndPermission(role, permission)) {
            throw PermissionException.permissionInUse();
        }

        RolePermission rp = RolePermission.create(role, permission);

        RolePermission saved = rolePermissionWriter.save(rp);

        projectionHandler.onRolePermissionsChanged(role);

        return RolePermissionResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional
    public RolePermissionListResponseDto addPermissions(final Long roleId, final List<Long> permissionIds) {

        Role role = roleReader.findById(roleId);

        List<Permission> permissions =
                permissionIds.stream().map(permissionReader::findById).toList();

        // 중복 검증
        for (Permission p : permissions) {
            if (rolePermissionReader.existsByRoleAndPermission(role, p)) {
                throw PermissionException.permissionInUse();
            }
        }

        List<RolePermission> newList =
                permissions.stream().map(p -> RolePermission.create(role, p)).toList();

        List<RolePermission> saved = rolePermissionWriter.saveAll(newList);

        projectionHandler.onRolePermissionsChanged(role);

        return RolePermissionListResponseDto.fromEntities(roleId, saved);
    }

    @Override
    @Transactional
    public RolePermissionListResponseDto replacePermissions(
            final Long roleId, final List<Long> permissionIds, final Long userId) {

        Role role = roleReader.findById(roleId);

        // soft delete 기존 항목들
        List<RolePermission> existed = rolePermissionReader.findAllByRole(role);
        existed.forEach(rp -> rp.softDelete(userId));
        rolePermissionWriter.saveAll(existed);

        // 새로운 매핑 생성 후 반환
        RolePermissionListResponseDto response = addPermissions(roleId, permissionIds);

        // Redis 업데이트
        projectionHandler.onRolePermissionsChanged(role);

        return response;
    }

    @Override
    @Transactional
    public RolePermissionListResponseDto removePermission(
            final Long roleId, final Long permissionId, final Long deleterId) {

        Role role = roleReader.findById(roleId);

        List<RolePermission> list = rolePermissionReader.findAllByRole(role).stream()
                .filter(rp -> rp.getPermission().getId().equals(permissionId))
                .toList();

        if (list.isEmpty()) {
            throw PermissionException.permissionNotFound();
        }

        list.forEach(rp -> rp.softDelete(deleterId));
        rolePermissionWriter.saveAll(list);

        // 최신 상태 반환
        List<RolePermission> remained = rolePermissionReader.findAllByRole(role);

        projectionHandler.onRolePermissionsChanged(role);

        return RolePermissionListResponseDto.fromEntities(roleId, remained);
    }
}
