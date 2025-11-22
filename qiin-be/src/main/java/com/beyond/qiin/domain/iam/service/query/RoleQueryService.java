package com.beyond.qiin.domain.iam.service.query;

import com.beyond.qiin.domain.iam.dto.role.response.RoleListResponseDto;
import com.beyond.qiin.domain.iam.dto.role.response.RoleResponseDto;

public interface RoleQueryService {
    // 역할 단건 조회
    RoleResponseDto getRole(final Long roleId);

    // 역할 목록 조회
    RoleListResponseDto getRoles();
}
