package com.beyond.qiin.domain.auth.service.command;

import com.beyond.qiin.domain.auth.dto.request.LoginRequestDto;
import com.beyond.qiin.domain.auth.dto.response.LoginResponseDto;
import com.beyond.qiin.domain.auth.dto.response.LoginResult;
import com.beyond.qiin.domain.auth.dto.response.TempPwLoginResponseDto;
import com.beyond.qiin.domain.auth.dto.response.TokenPairResponseDto;
import com.beyond.qiin.domain.auth.exception.AuthException;
import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.exception.UserException;
import com.beyond.qiin.domain.iam.support.user.UserReader;
import com.beyond.qiin.domain.iam.support.userrole.UserRoleReader;
import com.beyond.qiin.internal.auth.dto.UserRoleContextDto;
import com.beyond.qiin.security.jwt.JwtTokenProvider;
import com.beyond.qiin.security.jwt.RedisTokenRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
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

        final User user = getUserOrThrow(request.getLoginKey());
        validatePassword(request.getPassword(), user.getPassword());
        final Long userId = user.getId();

        final UserRoleContextDto ctx = getUserRoleContext(userId);

        // access + refresh = 하나의 헬퍼에서 발급
        TokenPairResponseDto tokens = issueTokenPair(userId, ctx.getRole(), user.getEmail(), ctx.getPermissions());

        user.validateTempPasswordUsage();
        if (user.isTempPassword() && user.isFirstLogin()) {
            // user.updateLastLoginAt(Instant.now());
            return TempPwLoginResponseDto.fromEntity(user, tokens.getAccess());
        }

        user.updateLastLoginAt(Instant.now());

        return LoginResponseDto.of(user, ctx.getRole(), tokens.getAccess(), tokens.getRefresh());
    }

    @Override
    @Transactional
    public void logout(final String accessToken) {

        final Long userId = jwtTokenProvider.getUserId(accessToken);

        // 리프레시 토큰 폐기
        redisTokenRepository.deleteRefreshToken(userId);

        long expiresIn = jwtTokenProvider.getRemainingValidityMillis(accessToken);
        redisTokenRepository.blacklistAccessToken(accessToken, Duration.ofMillis(expiresIn));
    }

    @Override
    @Transactional
    public LoginResult refresh(final String refreshToken) {

        validateRefreshToken(refreshToken);

        final Long userId = jwtTokenProvider.getUserId(refreshToken);
        final User user = userReader.findById(userId);

        final UserRoleContextDto ctx = getUserRoleContext(userId);

        final TokenPairResponseDto newTokens =
                issueTokenPair(userId, ctx.getRole(), user.getEmail(), ctx.getPermissions());

        return LoginResponseDto.of(user, ctx.getRole(), newTokens.getAccess(), newTokens.getRefresh());
    }

    // -----------------------
    // 헬퍼 메서드
    // -----------------------

    // 이메일로 유저 검증
    private User getUserOrThrow(final String loginKey) {
        return userReader.findByEmail(loginKey);
    }

    // 리프레시 토큰 검증
    private void validateRefreshToken(final String refreshToken) {
        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw AuthException.tokenExpired();
        }

        final Long userId = jwtTokenProvider.getUserId(refreshToken);
        final String storedToken = redisTokenRepository.getRefreshToken(userId);

        if (!refreshToken.equals(storedToken)) {
            throw AuthException.unauthorized();
        }
    }

    // 비밀번호 일치 여부 검증
    private void validatePassword(final String raw, final String encoded) {
        if (!passwordEncoder.matches(raw, encoded)) {
            throw UserException.invalidPassword();
        }
    }

    // 사용자 역할 및 권한 조회
    private UserRoleContextDto getUserRoleContext(final Long userId) {
        String role = userRoleReader.findRoleNameByUserId(userId);
        List<String> permissions = userRoleReader.findPermissionsByUserId(userId);
        return UserRoleContextDto.of(role, permissions);
    }

    // AccessToken + RefreshToken 동시 발급
    private TokenPairResponseDto issueTokenPair(
            final Long userId, final String role, final String email, final List<String> permissions) {
        final String access = jwtTokenProvider.generateAccessToken(userId, role, email, permissions);
        final String refresh = jwtTokenProvider.generateRefreshToken(userId, role);

        redisTokenRepository.saveRefreshToken(
                userId, refresh, Duration.ofMillis(jwtTokenProvider.getRefreshTokenValidityMillis()));

        return TokenPairResponseDto.of(access, refresh);
    }
}
