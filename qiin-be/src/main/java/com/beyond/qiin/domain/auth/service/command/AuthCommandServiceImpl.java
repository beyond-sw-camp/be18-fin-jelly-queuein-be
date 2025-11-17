package com.beyond.qiin.domain.auth.service.command;

import com.beyond.qiin.domain.auth.dto.request.LoginRequestDto;
import com.beyond.qiin.domain.auth.dto.response.LoginResponseDto;
import com.beyond.qiin.domain.auth.dto.response.LoginResult;
import com.beyond.qiin.domain.auth.dto.response.TempPwLoginResponseDto;
import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.exception.UserException;
import com.beyond.qiin.domain.iam.support.user.UserReader;
import com.beyond.qiin.domain.iam.support.userrole.UserRoleReader;
import com.beyond.qiin.security.jwt.JwtTokenProvider;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthCommandServiceImpl implements AuthCommandService {

    private final UserReader userReader;
    private final UserRoleReader userRoleReader;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public LoginResult login(final LoginRequestDto request) {

        // 로그인 키 확장성 유지 (현재는 email 기반)
        final String loginKey = request.getLoginKey();

        // 사용자 조회
        final User user = userReader.findByEmail(loginKey);

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw UserException.invalidPassword();
        }

        // 사용자 역할 조회 (v1: 단일 역할)
        final String role = userRoleReader.findRoleNameByUserId(user.getId());

        // JWT 발급
        final String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), role);

        // 임시 비밀번호 사용 여부 확인
        // 최초 로그인(lastLoginAt == null) → 로그인 허용
        // 그 이후 → 로그인 차단
        if (Boolean.TRUE.equals(user.getPasswordExpired())) {

            // 최초 로그인 (lastLoginAt == null) → 비밀번호 변경 안내
            if (user.getLastLoginAt() == null) {
                return TempPwLoginResponseDto.fromEntity(user, accessToken);
            }

            // 최초 로그인이 아닌데도 passwordExpired=true → 차단
            throw UserException.passwordExpired();
        }

        // 리프레시 토큰 발급
        final String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), role);

        // 로그인 시각 업데이트
        user.updateLastLoginAt(Instant.now());

        // DTO 응답 생성
        return LoginResponseDto.of(user, role, accessToken, refreshToken);
    }
}
