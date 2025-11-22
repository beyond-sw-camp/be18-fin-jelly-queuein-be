package com.beyond.qiin.security.login;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Slf4j
public final class JsonLoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final org.springframework.security.core.AuthenticationException exception)
            throws IOException {

        log.warn("[LoginFailure] {}", exception.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        response.getWriter()
                .write(
                        """
            {
              "status": 401,
              "message": "로그인 실패: %s"
            }
            """
                                .formatted(exception.getMessage()));
    }
}
