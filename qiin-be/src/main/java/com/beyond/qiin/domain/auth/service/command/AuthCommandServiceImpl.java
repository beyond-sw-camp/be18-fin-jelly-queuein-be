package com.beyond.qiin.domain.auth.service.command;

import com.beyond.qiin.domain.auth.dto.request.SignupRequestDto;
import com.beyond.qiin.domain.auth.dto.response.SignupResponseDto;
import com.beyond.qiin.domain.auth.exception.AuthException;
import com.beyond.qiin.domain.auth.exception.AuthException.AuthErrorCode;
import com.beyond.qiin.domain.iam.entity.Role;
import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.entity.UserRole;
import com.beyond.qiin.domain.iam.repository.RoleJpaRepository;
import com.beyond.qiin.domain.iam.repository.UserJpaRepository;
import com.beyond.qiin.domain.iam.repository.UserRoleJpaRepository;
import com.beyond.qiin.security.PasswordGenerator;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthCommandServiceImpl implements AuthCommandService {

    private final UserJpaRepository userJpaRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final UserRoleJpaRepository userRoleJpaRepository;
    private final StringRedisTemplate redisTemplate;

    @Override
    @Transactional
    public SignupResponseDto createMaster(final SignupRequestDto request) {

        // 임시 비밀번호 생성
        final String tempPassword = PasswordGenerator.generate();

        // 비밀번호 암호화 (NoOp 임시 사용)
        final String encrypted = "{noop}" + tempPassword;

        // DTO → User 엔티티 변환
        final User user = request.toEntity(encrypted);
        final User saved = userJpaRepository.save(user);

        // MASTER 역할 조회
        final Role masterRole = roleJpaRepository
                .findByRoleName("MASTER")
                .orElseThrow(() -> new AuthException(AuthErrorCode.ROLE_NOT_FOUND));

        // UserRole 매핑 저장
        userRoleJpaRepository.save(
                UserRole.builder().role(masterRole).user(saved).build());

        // Redis에 임시 비밀번호 저장 (10분)
        redisTemplate.opsForValue().set("TEMP_PASSWORD:" + saved.getId(), tempPassword, Duration.ofMinutes(10));

        // 응답 생성
        return SignupResponseDto.fromEntity(saved, masterRole);
    }
}
