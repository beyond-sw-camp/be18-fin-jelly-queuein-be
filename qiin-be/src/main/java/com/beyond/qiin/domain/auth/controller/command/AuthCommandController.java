package com.beyond.qiin.domain.auth.controller.command;

import com.beyond.qiin.domain.auth.dto.request.LoginRequestDto;
import com.beyond.qiin.domain.auth.dto.response.LoginResult;
import com.beyond.qiin.domain.auth.service.command.AuthCommandService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthCommandController {

    private final AuthCommandService authCommandService;

    @PostMapping("/login")
    public ResponseEntity<LoginResult> login(@RequestBody final LoginRequestDto request) {
        return ResponseEntity.ok(authCommandService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(final HttpServletRequest request) {

        authCommandService.logout(request);

        return ResponseEntity.ok().build();
    }
}
