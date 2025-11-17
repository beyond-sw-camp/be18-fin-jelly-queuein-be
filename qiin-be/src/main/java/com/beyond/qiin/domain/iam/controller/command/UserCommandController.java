package com.beyond.qiin.domain.iam.controller.command;

import com.beyond.qiin.domain.iam.dto.user.request.ChangePasswordRequestDto;
import com.beyond.qiin.domain.iam.dto.user.request.CreateUserRequestDto;
import com.beyond.qiin.domain.iam.dto.user.request.UpdateUserRequestDto;
import com.beyond.qiin.domain.iam.service.command.UserCommandService;
import com.beyond.qiin.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserCommandController {

    private final UserCommandService userCommandService;

    // 사용자 생성
    @PostMapping
    @PreAuthorize("hasAnyAuthority('MASTER','ADMIN')")
    public ResponseEntity<Void> createUser(@RequestBody final CreateUserRequestDto request) {
        userCommandService.createUser(request);
        return ResponseEntity.ok().build();
    }

    // 사용자 수정
    @PatchMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('MASTER','ADMIN','MANAGER')")
    public ResponseEntity<Void> updateUser(
            @PathVariable final Long userId, @RequestBody final UpdateUserRequestDto request) {
        userCommandService.updateUser(userId, request);
        return ResponseEntity.ok().build();
    }

    // 본인 비밀번호 변경
    @PatchMapping("/me/password")
    public ResponseEntity<Void> changeMyPassword(@RequestBody final ChangePasswordRequestDto request) {
        final Long userId = SecurityUtils.getCurrentUserId();
        userCommandService.changePassword(userId, request);
        return ResponseEntity.ok().build();
    }

    // 사용자 삭제
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('MASTER','ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userCommandService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }
}
