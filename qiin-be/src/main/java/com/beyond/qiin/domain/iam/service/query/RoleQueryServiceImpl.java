package com.beyond.qiin.domain.iam.service.query;

import com.beyond.qiin.domain.iam.dto.role.response.RoleListResponseDto;
import com.beyond.qiin.domain.iam.dto.role.response.RoleResponseDto;
import com.beyond.qiin.domain.iam.entity.Role;
import com.beyond.qiin.domain.iam.support.role.RoleReader;
import com.beyond.qiin.infra.redis.iam.role.RoleReadModel;
import com.beyond.qiin.infra.redis.iam.role.RoleRedisAdapter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleQueryServiceImpl implements RoleQueryService {

    private final RoleReader roleReader; // DB
    private final RoleRedisAdapter redisAdapter; // Redis

    // 역할 단건 조회
    @Override
    @Transactional(readOnly = true)
    public RoleResponseDto getRole(final Long roleId) {

        RoleReadModel read = redisAdapter.findById(roleId);
        if (read != null) {
            log.info("[RoleQuery] Redis HIT for roleId={}", roleId);
            return new RoleResponseDto(read.getRoleId(), read.getRoleDescription(), read.getRoleName());
        }

        log.info("[RoleQuery] Redis MISS for roleId={}, loading from DB", roleId);

        Role role = roleReader.findById(roleId);
        redisAdapter.save(role);

        return RoleResponseDto.fromEntity(role);
    }

    // 역할 목록 조회
    @Override
    @Transactional(readOnly = true)
    public RoleListResponseDto getRoles() {

        List<Role> roles = roleReader.findAll();
        roles.forEach(redisAdapter::save);

        return RoleListResponseDto.builder()
                .roles(roles.stream().map(RoleResponseDto::fromEntity).toList())
                .build();
    }
}
