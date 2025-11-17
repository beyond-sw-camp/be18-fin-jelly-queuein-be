package com.beyond.qiin.internal.master.service;

import com.beyond.qiin.domain.iam.entity.Role;
import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.entity.UserRole;
import com.beyond.qiin.domain.iam.exception.RoleException;
import com.beyond.qiin.domain.iam.exception.UserException;
import com.beyond.qiin.domain.iam.repository.RoleJpaRepository;
import com.beyond.qiin.domain.iam.repository.UserJpaRepository;
import com.beyond.qiin.domain.iam.repository.UserRoleJpaRepository;
import com.beyond.qiin.internal.master.dto.request.RegisterMasterRequestDto;
import com.beyond.qiin.internal.master.dto.response.RegisterMasterResponseDto;
import com.beyond.qiin.security.PasswordGenerator;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MasterServiceImpl implements MasterService {

    private final UserJpaRepository userJpaRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final UserRoleJpaRepository userRoleJpaRepository;
    private final StringRedisTemplate redisTemplate;

    @Override
    @Transactional
    public RegisterMasterResponseDto createMaster(final RegisterMasterRequestDto request) {

        // MASTER 중복 체크
        boolean hasMaster = userRoleJpaRepository.existsByRole_RoleName("MASTER");
        if (hasMaster) {
            throw UserException.userAlreadyExists();
        }

        // 임시 비밀번호 생성
        final String tempPassword = PasswordGenerator.generate();

        // 비밀번호 암호화 (NoOp 임시 사용)
        // TODO: BCrypt로 변경 예정
        final String encrypted = "{noop}" + tempPassword;

        // DTO → User 엔티티 변환
        final User user = request.toEntity(encrypted);
        final User saved = userJpaRepository.save(user);

        // MASTER 역할 조회
        final Role masterRole = roleJpaRepository.findByRoleName("MASTER").orElseThrow(RoleException::roleNotFound);

        // UserRole 매핑 저장
        userRoleJpaRepository.save(
                UserRole.builder().role(masterRole).user(saved).build());

        // Redis에 임시 비밀번호 저장 (10분)
        redisTemplate.opsForValue().set("TEMP_PASSWORD:" + saved.getId(), tempPassword, Duration.ofMinutes(10));

        // 응답 생성
        return RegisterMasterResponseDto.fromEntity(saved, masterRole);
    }
}
