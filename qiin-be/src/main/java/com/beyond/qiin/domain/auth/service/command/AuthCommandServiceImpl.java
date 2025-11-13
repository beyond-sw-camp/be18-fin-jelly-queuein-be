package com.beyond.qiin.domain.auth.service.command;

import com.beyond.qiin.domain.auth.dto.request.SignupRequestDto;
import com.beyond.qiin.domain.auth.dto.response.SignupResponseDto;
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

        // 1. 임시 비밀번호 생성
        String tempPassword = PasswordGenerator.generate();

        // 2. 암호화 (SecurityConfig 만들면 PasswordEncoder 주입)
        String encrypted = "{noop}" + tempPassword; // 임시로 NoOp
        // 실제는 passwordEncoder.encode(tempPassword)

        // 3. 유저 생성
        User user = User.builder()
                .dptId(request.getDptId())
                .userNo(request.getUserNo())
                .email(request.getEmail())
                .password(encrypted)
                .passwordExpired(true)
                .build();

        User saved = userJpaRepository.save(user);

        // 4. 역할(MASTER) 조회
        Role masterRole = roleJpaRepository
                .findByRoleName("MASTER")
                .orElseThrow(() -> new RuntimeException("MASTER role not found"));

        // 5. UserRole 저장
        UserRole mapping = UserRole.builder().role(masterRole).user(saved).build();

        userRoleJpaRepository.save(mapping);

        // 6. Redis에 임시 비밀번호 저장 (10분 TTL)
        redisTemplate.opsForValue().set("TEMP_PASSWORD:" + saved.getId(), tempPassword, Duration.ofMinutes(10));

        // 7. 반환
        return SignupResponseDto.builder()
                .userId(saved.getId())
                .email(saved.getEmail())
                .role(masterRole.getRoleName())
                .build();
    }
}
