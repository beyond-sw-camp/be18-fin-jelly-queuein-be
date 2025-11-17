package com.beyond.qiin.domain.iam.controller.command;

import com.beyond.qiin.domain.iam.dto.user.request.ChangePasswordRequestDto;
import com.beyond.qiin.domain.iam.service.command.UserCommandService;
import com.beyond.qiin.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserCommandController {

    private final UserCommandService userCommandService;

    // 본인 비밀번호 변경
    @PatchMapping("/me/password")
    public ResponseEntity<Void> changeMyPassword(@RequestBody final ChangePasswordRequestDto request) {
        final Long userId = SecurityUtils.getCurrentUserId();
        userCommandService.changePassword(userId, request);
        return ResponseEntity.ok().build();
    }
}
