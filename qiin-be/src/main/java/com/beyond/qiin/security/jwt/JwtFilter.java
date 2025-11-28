package com.beyond.qiin.security.jwt;

import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.repository.UserJpaRepository;
import com.beyond.qiin.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    private final UserJpaRepository userJpaRepository;
    private final RedisTokenRepository redisTokenRepository;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
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
            log.warn("[JwtFilter] 블랙리스트 토큰으로 요청 차단");
            filterChain.doFilter(request, response);
            return;
        }

        // RefreshToken은 인증에 사용하면 안 됨
        if (jwtTokenProvider.validateAccessToken(token)) {
            try {
                Claims claims = jwtTokenProvider.getClaims(token);
                Long userId = Long.valueOf(claims.getSubject());

                // TODO: DB 조회 제거
                User user = userJpaRepository.findById(userId).orElse(null);

                String role = (String) claims.get("role");

                if (user != null) {
                    UsernamePasswordAuthenticationToken authentication =
                            getUsernamePasswordAuthenticationToken(userId, user, role);

                    // AccessToken 저장
                    authentication.setDetails(token);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }

            } catch (Exception e) {
                log.warn("[JwtFilter] Token processing failed: {}", e.getMessage());
                authenticationEntryPoint.commence(
                        request, response, new InsufficientAuthenticationException("유효하지 않은 토큰입니다."));
                return;
            }
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
