package com.beyond.qiin.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();

        // 테스트용 환경변수 주입
        ReflectionTestUtils.setField(
                jwtTokenProvider,
                "secretKey",
                "mySuperSecretKeyForJwtToken1234567890_mySuperSecretKeyForJwtToken1234567890");
        ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenExpiration", 60_000L);
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenExpiration", 120_000L);

        jwtTokenProvider.init();
    }

    @Test
    void testGenerateAndValidateAccessToken() {
        String accessToken = jwtTokenProvider.generateAccessToken(1L, "ADMIN");
        assertThat(accessToken).isNotNull();

        boolean isValid = jwtTokenProvider.validateAccessToken(accessToken);
        assertThat(isValid).isTrue();

        Long userId = jwtTokenProvider.getUserId(accessToken);
        String role = jwtTokenProvider.getUserRole(accessToken);
        String type = jwtTokenProvider.getTokenType(accessToken);

        assertThat(userId).isEqualTo(1L);
        assertThat(role).isEqualTo("ADMIN");
        assertThat(type).isEqualTo("ACCESS");
    }

    @Test
    void testGenerateAndValidateRefreshToken() {
        String refreshToken = jwtTokenProvider.generateRefreshToken(1L, "ADMIN");
        assertThat(refreshToken).isNotNull();

        boolean isValid = jwtTokenProvider.validateRefreshToken(refreshToken);
        assertThat(isValid).isTrue();

        String type = jwtTokenProvider.getTokenType(refreshToken);
        assertThat(type).isEqualTo("REFRESH");
    }

    @Test
    void testInvalidTokenShouldFail() {
        String invalidToken = "invalid.token.value";
        boolean result = jwtTokenProvider.validateAccessToken(invalidToken);
        assertThat(result).isFalse();
    }
}
