package com.beyond.qiin.domain.iam.service.query;

import com.beyond.qiin.domain.iam.dto.permission.response.PermissionListResponseDto;
import com.beyond.qiin.domain.iam.dto.permission.response.PermissionResponseDto;
import com.beyond.qiin.domain.iam.entity.Permission;
import com.beyond.qiin.domain.iam.support.permission.PermissionReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PermissionQueryServiceImpl implements PermissionQueryService {

    private final PermissionReader permissionReader;

    // 권한 상세 조회
    @Override
    @Transactional(readOnly = true)
    public PermissionResponseDto getPermission(final Long permissionId) {
        Permission permission = permissionReader.findById(permissionId);
        return PermissionResponseDto.fromEntity(permission);
    }

    // 권한 목록 조회
    @Override
    @Transactional(readOnly = true)
    public PermissionListResponseDto getPermissions() {
        return PermissionListResponseDto.from(permissionReader.findAll());
    }
}
