package com.beyond.qiin.security.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor
public final class JsonLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    @Override
    public org.springframework.security.core.Authentication attemptAuthentication(
            final HttpServletRequest request, final HttpServletResponse response) {

        try {
            final LoginRequestDto loginRequest =
                    new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

            final String email = loginRequest.getEmail();
            final String password = loginRequest.getPassword();

            log.info("[JsonLoginFilter] login attempt email={}", email);

            final UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(email, password);

            return authenticationManager.authenticate(authToken);

        } catch (IOException e) {
            throw new RuntimeException("로그인 요청 파싱 실패", e);
        }
    }
}
