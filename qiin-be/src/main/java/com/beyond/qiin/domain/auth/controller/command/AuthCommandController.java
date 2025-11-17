package com.beyond.qiin.domain.auth.controller.command;

import com.beyond.qiin.domain.auth.service.command.AuthCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthCommandController {

    private final AuthCommandService authCommandService;
}
