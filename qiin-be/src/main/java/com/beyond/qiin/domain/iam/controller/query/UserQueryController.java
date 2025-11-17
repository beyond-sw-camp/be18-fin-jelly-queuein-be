package com.beyond.qiin.domain.iam.controller.query;

import com.beyond.qiin.domain.iam.dto.user.response.DetailUserResponseDto;
import com.beyond.qiin.domain.iam.dto.user.response.ListUserResponseDto;
import com.beyond.qiin.domain.iam.service.query.UserQueryService;
import com.beyond.qiin.security.SecurityUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserQueryController {

    private final UserQueryService userQueryService;

    // 전체 조회 (MASTER, ADMIN, MANAGER)
    @GetMapping
    @PreAuthorize("hasAnyAuthority('MASTER','ADMIN','MANAGER')")
    public List<ListUserResponseDto> getUsers() {
        return userQueryService.getUsers();
    }

    // 사용자 상세조회
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('MASTER','ADMIN','MANAGER','GENERAL')")
    public DetailUserResponseDto getUser(@PathVariable Long userId) {
        return userQueryService.getUser(userId);
    }

    // 내 정보 조회
    @GetMapping("/me")
    public DetailUserResponseDto getMe() {
        Long userId = SecurityUtils.getCurrentUserId();
        return userQueryService.getUser(userId);
    }
}
