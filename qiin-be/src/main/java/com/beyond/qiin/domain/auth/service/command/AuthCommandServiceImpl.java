package com.beyond.qiin.domain.auth.service.command;

import com.beyond.qiin.domain.auth.dto.request.LoginRequestDto;
import com.beyond.qiin.domain.auth.dto.response.LoginResponseDto;
import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.exception.RoleException;
import com.beyond.qiin.domain.iam.exception.UserException;
import com.beyond.qiin.domain.iam.repository.UserJpaRepository;
import com.beyond.qiin.domain.iam.repository.UserRoleJpaRepository;
import com.beyond.qiin.security.jwt.JwtTokenProvider;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthCommandServiceImpl implements AuthCommandService {

    private final UserJpaRepository userJpaRepository;
    private final UserRoleJpaRepository userRoleJpaRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public LoginResponseDto login(final LoginRequestDto request) {

        // 로그인 키 확장성 유지 (현재는 email 기반)
        final String loginKey = request.getLoginKey();

        // 사용자 조회
        final User user = userJpaRepository.findByEmail(loginKey).orElseThrow(UserException::userNotFound);

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw UserException.invalidPassword();
        }

        // 임시 비밀번호 사용 여부 확인
        if (Boolean.TRUE.equals(user.getPasswordExpired())) {
            throw UserException.passwordExpired();
        }

        // 사용자 역할 조회 (v1: 단일 역할)
        final String role = userRoleJpaRepository
                .findTopByUser_Id(user.getId())
                .map(ur -> ur.getRole().getRoleName())
                .orElseThrow(RoleException::roleNotFound);

        // JWT 발급
        final String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), role);
        final String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), role);

        // 로그인 시각 업데이트
        user.updateLastLoginAt(Instant.now());

        // DTO 응답 생성
        return LoginResponseDto.of(user, role, accessToken, refreshToken);
    }
}
