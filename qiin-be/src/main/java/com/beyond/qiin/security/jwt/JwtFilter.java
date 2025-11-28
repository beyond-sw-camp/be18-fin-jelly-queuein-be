package com.beyond.qiin.security.jwt;

import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.infra.redis.iam.permission.PermissionCacheAdapter;
import com.beyond.qiin.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final PermissionCacheAdapter permissionCache;
    private final RedisTokenRepository redisTokenRepository;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    private static final Duration PERMISSION_TTL = Duration.ofMinutes(10);

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
            throws ServletException, IOException {

        final String header = request.getHeader("Authorization");
        final boolean hasHeader = header != null;
        final boolean isBearer = hasHeader && header.startsWith("Bearer ");

        log.debug(
                "[JwtFilter] 요청 URI: {}, hasAuthorization={}, authPrefix={}",
                request.getRequestURI(),
                hasHeader,
                isBearer ? "Bearer" : "none");

        // Authorization 헤더 없거나 Bearer 아니면 바로 패스
        if (!isBearer) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = header.substring(7);

        // 블랙리스트 토큰이면 인증 없이 다음 필터로 넘김
        if (redisTokenRepository.isBlacklisted(token)) {
            authenticationEntryPoint.commence(
                    request, response, new InsufficientAuthenticationException("블랙리스트 처리된 토큰입니다."));
            return;
        }

        if (!jwtTokenProvider.validateAccessToken(token)) {
            authenticationEntryPoint.commence(
                    request, response, new InsufficientAuthenticationException("유효하지 않은 토큰입니다."));
            return;
        }

        try {
            Claims claims = jwtTokenProvider.getClaims(token);
            Long userId = Long.valueOf(claims.getSubject());
            String role = claims.get("role", String.class);
            final String email = claims.get("email", String.class);
            List<String> permissions = claims.get("permissions", List.class);

            // JWT payload에 permissions 없으면 캐시 조회
            if (permissions == null) {
                permissions = permissionCache.get(userId);
            }

            if (permissions == null) {
                permissions = List.of();
            }

            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(role));
            permissions.forEach(p -> authorities.add(new SimpleGrantedAuthority(p)));

            CustomUserDetails userDetails = new CustomUserDetails(userId, email, authorities);

            // 인증 객체 설정
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

            // AccessToken 저장
            authentication.setDetails(token);

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (final Exception e) {
            log.warn("[JwtFilter] Token processing failed: {}", e.getMessage());
            authenticationEntryPoint.commence(
                    request, response, new InsufficientAuthenticationException("유효하지 않은 토큰입니다."));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private static UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(
            final Long userId, final User user, final String role) {
        CustomUserDetails userDetails =
                new CustomUserDetails(userId, user.getEmail(), List.of(new SimpleGrantedAuthority(role)));

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        return authentication;
    }
}
