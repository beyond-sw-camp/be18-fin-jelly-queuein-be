package com.beyond.qiin.internal.master.service;

import com.beyond.qiin.domain.iam.entity.Role;
import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.entity.UserRole;
import com.beyond.qiin.domain.iam.exception.UserException;
import com.beyond.qiin.domain.iam.support.role.RoleReader;
import com.beyond.qiin.domain.iam.support.user.UserWriter;
import com.beyond.qiin.domain.iam.support.userrole.UserRoleReader;
import com.beyond.qiin.domain.iam.support.userrole.UserRoleWriter;
import com.beyond.qiin.internal.master.dto.request.RegisterMasterRequestDto;
import com.beyond.qiin.internal.master.dto.response.RegisterMasterResponseDto;
import com.beyond.qiin.security.util.PasswordGenerator;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MasterServiceImpl implements MasterService {

    private final UserWriter userWriter;
    private final RoleReader roleReader;
    private final UserRoleWriter userRoleWriter;
    private final UserRoleReader userRoleReader;

    private final StringRedisTemplate redisTemplate;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public RegisterMasterResponseDto createMaster(final RegisterMasterRequestDto request) {

        // MASTER 중복 체크
        boolean hasMaster = userRoleReader.existsMaster();
        if (hasMaster) {
            throw UserException.userAlreadyExists();
        }

        // 임시 비밀번호 생성
        final String tempPassword = PasswordGenerator.generate();

        // 비밀번호 암호화
        final String encrypted = passwordEncoder.encode(tempPassword);

        // DTO → User 엔티티 변환
        final User user = request.toEntity(encrypted);
        final User saved = userWriter.save(user);

        // MASTER 역할 조회
        final Role masterRole = roleReader.findByRoleName("MASTER");

        // UserRole 매핑 저장
        userRoleWriter.save(UserRole.builder().role(masterRole).user(saved).build());

        // Redis에 임시 비밀번호 저장 (10분)
        redisTemplate.opsForValue().set("TEMP_PASSWORD:" + saved.getId(), tempPassword, Duration.ofMinutes(10));

        // 응답 생성
        return RegisterMasterResponseDto.fromEntity(saved, masterRole, tempPassword);
    }
}
