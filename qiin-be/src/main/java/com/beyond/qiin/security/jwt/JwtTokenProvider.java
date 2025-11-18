package com.beyond.qiin.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${JWT_SECRET_KEY}")
    private String secretKey;

    @Value("${JWT_ACCESS_TOKEN_EXPIRATION}")
    private long accessTokenExpiration;

    @Value("${JWT_REFRESH_TOKEN_EXPIRATION}")
    private long refreshTokenExpiration;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        log.info("JWT SecretKey 초기화 완료");
    }

    // -------------------------------------------------------------
    // Token 생성
    // -------------------------------------------------------------

    /** Access Token 생성 */
    public String generateAccessToken(final Long userId, final String role) {
        return generateToken(userId, role, accessTokenExpiration, "ACCESS");
    }

    /** Refresh Token 생성 */
    public String generateRefreshToken(final Long userId, final String role) {
        return generateToken(userId, role, refreshTokenExpiration, "REFRESH");
    }

    /** 공통 토큰 생성 로직 */
    private String generateToken(
            final Long userId, final String role, final long expirationMillis, final String tokenType) {
        final Date now = new Date();
        final Date expiry = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("role", role)
                .claim("token_type", tokenType)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    // -------------------------------------------------------------
    //  Token 검증
    // -------------------------------------------------------------

    // Access Token 검증
    public boolean validateAccessToken(final String token) {
        return validateTokenInternal(token, "ACCESS");
    }

    // Refresh Token 검증
    public boolean validateRefreshToken(final String token) {
        return validateTokenInternal(token, "REFRESH");
    }

    // 공통 검증 로직
    private boolean validateTokenInternal(final String token, final String expectedType) {
        try {
            final Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            final String tokenType = claims.get("token_type", String.class);
            if (tokenType == null || !tokenType.equals(expectedType)) {
                log.warn("JWT 타입 불일치 — 기대: {}, 실제: {}", expectedType, tokenType);
                return false;
            }

            return true;
        } catch (ExpiredJwtException e) {
            log.warn("[Validation] {} 토큰 만료: {}", expectedType, e.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("[Validation] {} 토큰 유효하지 않음: {}", expectedType, e.getMessage());
        }
        return false;
    }

    // -------------------------------------------------------------
    // Token Claims 조회
    // -------------------------------------------------------------

    // 토큰에서 사용자 ID 추출
    public Long getUserId(final String token) {
        return Long.parseLong(getClaims(token).getSubject());
    }

    // 토큰에서 역할(Role) 추출
    public String getUserRole(final String token) {
        return getClaims(token).get("role", String.class);
    }

    // 어떤 토큰인지 타입 추출
    public String getTokenType(final String token) {
        return getClaims(token).get("token_type", String.class);
    }

    // Claims 공통 조회
    Claims getClaims(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Refresh Token TTL 조회
    public long getRefreshTokenValidityMillis() {
        return refreshTokenExpiration;
    }

    // Authorization 헤더에서 Access Token 추출
    public String resolveAccessToken(final HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    // Access Token 남은 유효 시간 계산
    public long getRemainingValidityMillis(final String token) {
        try {
            Claims claims = getClaims(token);
            long expiration = claims.getExpiration().getTime();
            long now = System.currentTimeMillis();
            return Math.max(expiration - now, 0);
        } catch (ExpiredJwtException e) {
            return 0;
        }
    }
}
