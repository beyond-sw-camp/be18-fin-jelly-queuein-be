package com.beyond.qiin.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            org.springframework.security.core.AuthenticationException authException)
            throws IOException {

        log.warn("[AuthEntryPoint] 인증 실패: {}", authException.getMessage());

        if (!response.isCommitted()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 에러
            response.setContentType("application/json;charset=UTF-8");

            Map<String, Object> body = Map.of(
                    "status", 401,
                    "code", "UNAUTHORIZED",
                    "message", "인증이 필요합니다.");

            new ObjectMapper().writeValue(response.getWriter(), body);
        }
    }
}
