package com.beyond.qiin.security.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class HealthCheckLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        String auth = request.getHeader("Authorization");

        // ALB 헬스체크인지 추정 가능한 패턴
        if (uri.equals("/") || uri.contains("health") || uri.contains("actuator")) {
            log.warn("[HEALTH-CHECK] uri={}, authHeader={}", uri, auth);
        } else {
            log.debug("[REQ] uri={}, method={}, auth={}", uri, request.getMethod(), auth);
        }

        filterChain.doFilter(request, response);

        // 응답 코드까지 로깅
        if (uri.equals("/") || uri.contains("health") || uri.contains("actuator")) {
            log.warn("[HEALTH-CHECK-RESP] status={}", response.getStatus());
        }
    }
}
