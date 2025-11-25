package com.beyond.qiin.domain.iam.service.command;

import com.beyond.qiin.domain.iam.dto.permission.request.CreatePermissionRequestDto;
import com.beyond.qiin.domain.iam.dto.permission.request.UpdatePermissionRequestDto;
import com.beyond.qiin.domain.iam.dto.permission.response.PermissionResponseDto;
import com.beyond.qiin.domain.iam.entity.Permission;
import com.beyond.qiin.domain.iam.exception.PermissionException;
import com.beyond.qiin.domain.iam.support.permission.PermissionReader;
import com.beyond.qiin.domain.iam.support.permission.PermissionWriter;
import com.beyond.qiin.domain.iam.support.rolepermission.RolePermissionReader;
import com.beyond.qiin.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PermissionCommandServiceImpl implements PermissionCommandService {
    private final PermissionReader permissionReader;
    private final PermissionWriter permissionWriter;

    private final RolePermissionReader rolePermissionReader;

    @Override
    @Transactional
    public PermissionResponseDto createPermission(final CreatePermissionRequestDto request) {

        permissionReader.validateDuplication(request.getPermissionName());

        Permission permission = Permission.create(request);

        Permission saved = permissionWriter.save(permission);

        return PermissionResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional
    public PermissionResponseDto updatePermission(final Long permissionId, final UpdatePermissionRequestDto request) {

        Permission permission = permissionReader.findById(permissionId);

        Permission updated = permissionWriter.save(permission);

        return PermissionResponseDto.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deletePermission(final Long permissionId) {

        Permission permission = permissionReader.findById(permissionId);

        if (rolePermissionReader.existsByPermission(permission)) {
            throw PermissionException.permissionInUse();
        }

        permission.delete(SecurityUtils.getCurrentUserId());
        permissionWriter.save(permission);
    }
}
