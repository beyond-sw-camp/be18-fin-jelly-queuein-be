package com.beyond.qiin.domain.auth.controller;

import com.beyond.qiin.domain.auth.dto.request.LoginRequestDto;
import com.beyond.qiin.domain.auth.dto.response.LoginResult;
import com.beyond.qiin.domain.auth.dto.response.LoginServiceResult;
import com.beyond.qiin.domain.auth.exception.AuthException;
import com.beyond.qiin.domain.auth.service.command.AuthCommandService;
import com.beyond.qiin.security.resolver.AccessToken;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthCommandService authCommandService;

    @Value("${JWT_REFRESH_TOKEN_EXPIRATION}")
    private long refreshTokenExpirationMs;

    private long cookieExpirySeconds() {
        return refreshTokenExpirationMs / 1000;
    }

    // HttpOnly Cookie 사용
    @PostMapping("/login")
    public ResponseEntity<LoginResult> login(final @Valid @RequestBody LoginRequestDto request) {

        LoginServiceResult result = authCommandService.login(request);

        String refreshToken = result.getRefreshToken();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(cookieExpirySeconds()) // @Value 사용 가능
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(result.getLoginResult());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AccessToken final String accessToken) {

        authCommandService.logout(accessToken);

        // 쿠키 삭제
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResult> refresh(
            final @CookieValue(value = "refreshToken", required = false) String refreshToken) {

        if (refreshToken == null || refreshToken.isBlank()) {
            throw AuthException.unauthorized(); // 혹은 전용 예외
        }

        // Service에서 access/refresh 재발급 처리
        LoginServiceResult result = authCommandService.refresh(refreshToken);

        // 새 refreshToken을 다시 쿠키로 내려줘야 함 (rotate 대응)
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", result.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(cookieExpirySeconds()) // @Value 기반 TTL 적용
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(result.getLoginResult());
    }
}
