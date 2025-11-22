package com.beyond.qiin.domain.iam.service.query;

import com.beyond.qiin.domain.iam.dto.role.response.RoleListResponseDto;
import com.beyond.qiin.domain.iam.dto.role.response.RoleResponseDto;
import com.beyond.qiin.domain.iam.support.role.RoleReader;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleQueryServiceImpl implements RoleQueryService {

    private final RoleReader roleReader;

    // 역할 단건 조회
    @Override
    @Transactional(readOnly = true)
    public RoleResponseDto getRole(final Long roleId) {
        return RoleResponseDto.fromEntity(roleReader.findById(roleId));
    }

    // 역할 목록 조회
    @Override
    @Transactional(readOnly = true)
    public RoleListResponseDto getRoles() {
        return RoleListResponseDto.builder()
                .roles(roleReader.findAll().stream()
                        .map(RoleResponseDto::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }
}
