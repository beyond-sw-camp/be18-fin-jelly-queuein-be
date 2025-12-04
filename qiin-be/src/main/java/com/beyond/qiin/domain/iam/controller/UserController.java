package com.beyond.qiin.domain.iam.controller;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.iam.dto.user.request.ChangePwRequestDto;
import com.beyond.qiin.domain.iam.dto.user.request.ChangeTempPwRequestDto;
import com.beyond.qiin.domain.iam.dto.user.request.CreateUserRequestDto;
import com.beyond.qiin.domain.iam.dto.user.request.UpdateMyInfoRequestDto;
import com.beyond.qiin.domain.iam.dto.user.request.UpdateUserByAdminRequestDto;
import com.beyond.qiin.domain.iam.dto.user.request.search_condition.GetUsersSearchCondition;
import com.beyond.qiin.domain.iam.dto.user.response.CreateUserResponseDto;
import com.beyond.qiin.domain.iam.dto.user.response.DetailUserResponseDto;
import com.beyond.qiin.domain.iam.dto.user.response.UserLookupResponseDto;
import com.beyond.qiin.domain.iam.dto.user.response.raw.RawUserListResponseDto;
import com.beyond.qiin.domain.iam.dto.user_role.request.UpdateUserRoleRequestDto;
import com.beyond.qiin.domain.iam.service.command.UserCommandService;
import com.beyond.qiin.domain.iam.service.query.UserQueryService;
import com.beyond.qiin.security.resolver.CurrentUserId;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    @PreAuthorize("hasAuthority('IAM_USER_CREATE')")
    public ResponseEntity<CreateUserResponseDto> createUser(@Valid @RequestBody final CreateUserRequestDto request) {
        return ResponseEntity.ok(userCommandService.createUser(request));
    }

    // 사용자 역할 수정
    @PatchMapping("/{userId}/role")
    @PreAuthorize("hasAuthority('IAM_USER_UPDATE')")
    public ResponseEntity<Void> updateUserRole(
            @PathVariable final Long userId,
            @RequestBody @Valid final UpdateUserRoleRequestDto request,
            @CurrentUserId final Long updaterId) {
        userCommandService.updateUserRole(userId, request.getRoleId(), updaterId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('MASTER','ADMIN')")
    public ResponseEntity<Void> updateUser(
            @PathVariable final Long userId, @Valid @RequestBody final UpdateUserByAdminRequestDto request) {

        userCommandService.updateUser(userId, request);
        return ResponseEntity.ok().build();
    }

    // 본인정보 수정
    @PatchMapping("/me")
    public ResponseEntity<Void> updateMe(
            @Valid @RequestBody final UpdateMyInfoRequestDto request, @CurrentUserId final Long userId) {

        userCommandService.updateMyInfo(userId, request);
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
    @PreAuthorize("hasAuthority('IAM_USER_DELETE')")
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
    public PageResponseDto<RawUserListResponseDto> searchUsers(
            @ModelAttribute final GetUsersSearchCondition condition, final Pageable pageable) {

        return userQueryService.searchUsers(condition, pageable);
    }

    // 참여자용 사용자 목록조회
    @GetMapping("/lookup")
    public ResponseEntity<List<UserLookupResponseDto>> lookupUsers(@RequestParam final String keyword) {

        return ResponseEntity.ok(userQueryService.lookupUsers(keyword));
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
