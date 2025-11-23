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

        log.info(
                "[JwtFilter] 요청 URI: {}, Authorization: {}",
                request.getRequestURI(),
                request.getHeader("Authorization"));

        final String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {

            final String token = header.substring(7);

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

                    User user = userJpaRepository.findById(userId).orElse(null);

                    String role = (String) claims.get("role");

                    if (user != null) {
                        CustomUserDetails userDetails = new CustomUserDetails(
                                userId, user.getEmail(), List.of(new SimpleGrantedAuthority(role)));

                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }

                } catch (Exception e) {
                    log.warn("[JwtFilter] Token processing failed: {}", e.getMessage());
                    authenticationEntryPoint.commence(
                            request, response, new InsufficientAuthenticationException("유효하지 않은 토큰입니다."));
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
