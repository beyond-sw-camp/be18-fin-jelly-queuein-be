package com.beyond.qiin.security.login;

import com.beyond.qiin.security.jwt.JwtTokenProvider;
import com.beyond.qiin.security.jwt.RedisTokenRepository;
import com.beyond.qiin.security.model.CustomUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Slf4j
@RequiredArgsConstructor
public final class JsonLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final RedisTokenRepository redisTokenRepository;

    @Override
    public void onAuthenticationSuccess(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final org.springframework.security.core.Authentication authentication)
            throws IOException, ServletException {

        final CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        final String role =
                user.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

        final String accessToken = tokenProvider.generateAccessToken(user.getUserId(), role);
        final String refreshToken = tokenProvider.generateRefreshToken(user.getUserId(), role);

        // Refresh Token 저장
        redisTokenRepository.saveRefreshToken(
                user.getUserId(), refreshToken, Duration.ofMillis(tokenProvider.getRefreshTokenValidityMillis()));

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter()
                .write(
                        """
        {
          "userId": %d,
          "email": "%s",
          "role": "%s",
          "accessToken": "%s",
          "refreshToken": "%s"
        }
        """
                                .formatted(user.getUserId(), user.getEmail(), role, accessToken, refreshToken));
    }
}
