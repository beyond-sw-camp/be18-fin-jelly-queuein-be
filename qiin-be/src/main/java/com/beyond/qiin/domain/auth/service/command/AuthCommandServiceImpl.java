package com.beyond.qiin.domain.auth.service.command;

import com.beyond.qiin.domain.auth.dto.request.LoginRequestDto;
import com.beyond.qiin.domain.auth.dto.response.LoginResponseDto;
import com.beyond.qiin.domain.auth.dto.response.LoginResult;
import com.beyond.qiin.domain.auth.dto.response.TempPwLoginResponseDto;
import com.beyond.qiin.domain.auth.exception.AuthException;
import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.exception.UserException;
import com.beyond.qiin.domain.iam.support.user.UserReader;
import com.beyond.qiin.domain.iam.support.userrole.UserRoleReader;
import com.beyond.qiin.security.jwt.JwtTokenProvider;
import com.beyond.qiin.security.jwt.RedisTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthCommandServiceImpl implements AuthCommandService {

    private final UserReader userReader;
    private final UserRoleReader userRoleReader;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RedisTokenRepository redisTokenRepository;

    @Override
    @Transactional
    public LoginResult login(final LoginRequestDto request) {

        // 로그인 키 확장성 유지 (현재는 email 기반)
        String loginKey = request.getLoginKey();

        // 사용자 조회
        final User user = userReader.findByEmail(loginKey);
        // final User user = userReader.findByUserNo(loginKey);

        // 비밀번호 검증
        validatePassword(request.getPassword(), user.getPassword());

        // 사용자 역할 조회 (v1: 단일 역할)
        final String role = userRoleReader.findRoleNameByUserId(user.getId());

        // JWT 발급
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), role);

        // 임시 비밀번호 사용 여부 확인
        user.validateTempPasswordUsage();
        // 최초 로그인(lastLoginAt == null) → 로그인 허용
        // 그 이후 → 로그인 차단
        if (user.isTempPassword() && user.isFirstLogin()) {
            return TempPwLoginResponseDto.fromEntity(user, accessToken);
        }

        // 리프레시 토큰 발급
        final String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), role);

        redisTokenRepository.saveRefreshToken(
                user.getId(), refreshToken, Duration.ofMillis(jwtTokenProvider.getRefreshTokenValidityMillis()));

        // 로그인 시각 업데이트
        user.updateLastLoginAt(Instant.now());

        // DTO 응답 생성
        return LoginResponseDto.of(user, role, accessToken, refreshToken);
    }

    @Override
    @Transactional
    public void logout(HttpServletRequest request) {

        // Access Token 추출
        String accessToken = jwtTokenProvider.resolveAccessToken(request);
        if (accessToken == null) {
            throw AuthException.unauthorized();
        }

        // 사용자 ID 추출
        final Long userId = jwtTokenProvider.getUserId(accessToken);

        // Refresh Token 삭제
        redisTokenRepository.deleteRefreshToken(userId);

        // Access Token 남은 시간 계산
        long expiresIn = jwtTokenProvider.getRemainingValidityMillis(accessToken);

        // Access Token 블랙리스트 추가
        redisTokenRepository.blacklistAccessToken(accessToken, Duration.ofMillis(expiresIn));
    }

    @Override
    @Transactional
    public LoginResult refresh(final String refreshToken) {

        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw AuthException.tokenExpired();
        }

        final Long userId = jwtTokenProvider.getUserId(refreshToken);

        final String storedToken = redisTokenRepository.getRefreshToken(userId);
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw AuthException.unauthorized();
        }

        final User user = userReader.findById(userId);
        final String role = userRoleReader.findRoleNameByUserId(userId);

        final String newAccess = jwtTokenProvider.generateAccessToken(userId, role);
        final String newRefresh = jwtTokenProvider.generateRefreshToken(userId, role);

        redisTokenRepository.saveRefreshToken(
                userId, newRefresh, Duration.ofMillis(jwtTokenProvider.getRefreshTokenValidityMillis()));

        return LoginResponseDto.of(user, role, newAccess, newRefresh);
    }

    // 비밀번호 검증 헬퍼 메서드
    private void validatePassword(final String rawPassword, final String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw UserException.invalidPassword();
        }
    }
}
