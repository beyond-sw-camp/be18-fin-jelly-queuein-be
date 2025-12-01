package com.beyond.qiin.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("JwtTokenProvider 단위 테스트")
public class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();

        String base64Secret =
                "qgWd2sFp3kH7yTz0cBmX4rJvE9uI6oQfYcL1xZb2aV5tU8hRnG0oD3jI7eM4lK9wS6yP1cVzT4rB8eYf0gL5dD==";

        ReflectionTestUtils.setField(jwtTokenProvider, "secretKey", base64Secret);
        ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenExpiration", 60_000L);
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenExpiration", 120_000L);

        jwtTokenProvider.init();
    }

    @Test
    @DisplayName("ACCESS 토큰 생성 및 검증테스트")
    void testGenerateAndValidateAccessToken() {
        String accessToken = jwtTokenProvider.generateAccessToken(
                1L, "ADMIN", "test@example.com", List.of("perm.read", "perm.write"));
        assertThat(accessToken).isNotNull();

        boolean isValid = jwtTokenProvider.validateAccessToken(accessToken);
        assertThat(isValid).isTrue();

        Long userId = jwtTokenProvider.getUserId(accessToken);
        String role = jwtTokenProvider.getUserRole(accessToken);
        String type = jwtTokenProvider.getTokenType(accessToken);

        assertThat(userId).isEqualTo(1L);
        assertThat(role).isEqualTo("ADMIN");
        assertThat(type).isEqualTo(JwtConstants.ACCESS);
    }

    @Test
    @DisplayName("REFRESH 토큰 생성 및 검증테스트")
    void testGenerateAndValidateRefreshToken() {
        String refreshToken = jwtTokenProvider.generateRefreshToken(1L, "ADMIN");
        assertThat(refreshToken).isNotNull();

        boolean isValid = jwtTokenProvider.validateRefreshToken(refreshToken);
        assertThat(isValid).isTrue();

        String type = jwtTokenProvider.getTokenType(refreshToken);
        assertThat(type).isEqualTo(JwtConstants.REFRESH);
    }

    @Test
    @DisplayName("유효하지 않은 토큰은 검증에 실패하는지 테스트")
    void testInvalidTokenShouldFail() {
        String invalidToken = "invalid.token.value";
        boolean result = jwtTokenProvider.validateAccessToken(invalidToken);
        assertThat(result).isFalse();
    }
}
