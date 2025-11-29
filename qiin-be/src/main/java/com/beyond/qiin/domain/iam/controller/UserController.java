package com.beyond.qiin.domain.iam.controller;

import com.beyond.qiin.domain.iam.dto.user.request.ChangePwRequestDto;
import com.beyond.qiin.domain.iam.dto.user.request.ChangeTempPwRequestDto;
import com.beyond.qiin.domain.iam.dto.user.request.CreateUserRequestDto;
import com.beyond.qiin.domain.iam.dto.user.request.UpdateUserRequestDto;
import com.beyond.qiin.domain.iam.dto.user.response.CreateUserResponseDto;
import com.beyond.qiin.domain.iam.dto.user.response.DetailUserResponseDto;
import com.beyond.qiin.domain.iam.dto.user.response.ListUserResponseDto;
import com.beyond.qiin.domain.iam.service.command.UserCommandService;
import com.beyond.qiin.domain.iam.service.query.UserQueryService;
import com.beyond.qiin.security.resolver.CurrentUserId;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    // -------------------------------------------
    // Command
    // -------------------------------------------

    // 사용자 생성
    @PostMapping
    @PreAuthorize("hasAnyAuthority('MASTER','ADMIN')")
    public ResponseEntity<CreateUserResponseDto> createUser(@Valid @RequestBody final CreateUserRequestDto request) {
        return ResponseEntity.ok(userCommandService.createUser(request));
    }

    // 사용자 수정
    @PatchMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('MASTER','ADMIN','MANAGER')")
    public ResponseEntity<Void> updateUser(
            @PathVariable final Long userId, @Valid @RequestBody final UpdateUserRequestDto request) {
        userCommandService.updateUser(userId, request);
        return ResponseEntity.ok().build();
    }

    // 임시 비밀번호 수정
    @PatchMapping("/me/temp-password")
    public ResponseEntity<Void> changeTempPassword(
            @Valid @RequestBody final ChangeTempPwRequestDto request, @CurrentUserId final Long userId) {

        userCommandService.changeTempPassword(userId, request.getNewPassword());

        return ResponseEntity.ok().build();
    }

    // 본인 비밀번호 변경
    @PatchMapping("/me/password")
    public ResponseEntity<Void> changeMyPassword(
            @Valid @RequestBody final ChangePwRequestDto request, @CurrentUserId final Long userId) {
        userCommandService.changePassword(userId, request);
        return ResponseEntity.ok().build();
    }

    // 사용자 삭제
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('MASTER','ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable final Long userId, @CurrentUserId final Long deleterId) {
        userCommandService.deleteUser(userId, deleterId);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------
    // Query
    // -------------------------------------------

    // 사용자 목록 조회 (MASTER, ADMIN, MANAGER)
    @GetMapping
    @PreAuthorize("hasAnyAuthority('MASTER','ADMIN','MANAGER')")
    public List<ListUserResponseDto> getUsers() {
        return userQueryService.getUsers();
    }

    // 사용자 상세 조회
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('MASTER','ADMIN','MANAGER')")
    public DetailUserResponseDto getUser(@PathVariable final Long userId) {
        return userQueryService.getUser(userId);
    }

    // 내 정보 조회
    @GetMapping("/me")
    public DetailUserResponseDto getMe(@CurrentUserId final Long userId) {
        return userQueryService.getUser(userId);
    }
}
