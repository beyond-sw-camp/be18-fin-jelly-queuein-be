package com.beyond.qiin.domain.iam.service.command;

import com.beyond.qiin.domain.iam.dto.role.request.CreateRoleRequestDto;
import com.beyond.qiin.domain.iam.dto.role.response.RoleResponseDto;
import com.beyond.qiin.domain.iam.entity.Role;
import com.beyond.qiin.domain.iam.support.role.RoleReader;
import com.beyond.qiin.domain.iam.support.role.RoleWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleCommandServiceImpl implements RoleCommandService {

    private final RoleReader roleReader;
    private final RoleWriter roleWriter;

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
}
