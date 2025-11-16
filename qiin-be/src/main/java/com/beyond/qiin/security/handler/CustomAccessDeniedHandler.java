package com.beyond.qiin.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException {

        log.warn("[AccessDenied] 권한 부족: {}", accessDeniedException.getMessage());

        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> body = Map.of(
                "status", 403,
                "code", "FORBIDDEN",
                "message", "접근 권한이 없습니다.");

        new ObjectMapper().writeValue(response.getWriter(), body);
    }
}
