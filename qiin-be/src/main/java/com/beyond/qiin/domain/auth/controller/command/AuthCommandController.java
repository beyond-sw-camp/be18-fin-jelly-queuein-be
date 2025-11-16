package com.beyond.qiin.domain.auth.controller.command;

import com.beyond.qiin.domain.auth.dto.request.SignupRequestDto;
import com.beyond.qiin.domain.auth.dto.response.SignupResponseDto;
import com.beyond.qiin.domain.auth.service.command.AuthCommandService;
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

    @PostMapping("/signup/masters")
    public ResponseEntity<SignupResponseDto> createMaster(@RequestBody final SignupRequestDto request) {
        return ResponseEntity.ok(authCommandService.createMaster(request));
    }
}
