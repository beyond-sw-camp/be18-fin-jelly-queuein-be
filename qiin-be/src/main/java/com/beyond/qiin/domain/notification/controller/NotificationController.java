package com.beyond.qiin.domain.notification.controller;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.notification.dto.NotificationResponseDto;
import com.beyond.qiin.domain.notification.entity.Notification;
import com.beyond.qiin.domain.notification.service.NotificationCommandService;
import com.beyond.qiin.domain.notification.support.NotificationReader;
import com.beyond.qiin.security.jwt.JwtTokenProvider;
import com.beyond.qiin.security.resolver.AccessToken;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final JwtTokenProvider jwtTokenProvider;

    private final NotificationCommandService notificationCommandService;
    private final NotificationReader notificationReader;

    // 개별 알림 조회
    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN', 'MANAGER', 'GENERAL')")
    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationResponseDto> getNotification(
            @PathVariable("notificationId") Long notificationId) {
        Notification notification = notificationReader.getNotification(notificationId);
        NotificationResponseDto notificationResponseDto = NotificationResponseDto.from(notification);

        return ResponseEntity.ok(notificationResponseDto);
    }

    // 사용자의 모든 알림 조회
    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN', 'MANAGER', 'GENERAL')")
    @GetMapping("/me")
    public ResponseEntity<PageResponseDto<NotificationResponseDto>> getAllNotifications(
            @AccessToken final String accessToken,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        final Long userId = jwtTokenProvider.getUserId(accessToken);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notification> notifications = notificationReader.getNotifications(userId, pageable);
        Page<NotificationResponseDto> dtoPage = notifications.map(NotificationResponseDto::from);
        PageResponseDto<NotificationResponseDto> response = PageResponseDto.from(dtoPage);

        return ResponseEntity.ok(response);
    }

    // 알림 읽음 처리
    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN', 'MANAGER', 'GENERAL')")
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId, @AccessToken final String accessToken) {
        final Long userId = jwtTokenProvider.getUserId(accessToken);
        notificationCommandService.markAsRead(notificationId, userId);
        return ResponseEntity.noContent().build(); // 204
    }

    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN', 'MANAGER', 'GENERAL')")
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable Long notificationId, @AccessToken final String accessToken) {
        final Long userId = jwtTokenProvider.getUserId(accessToken);
        notificationCommandService.softDelete(notificationId, userId);
        return ResponseEntity.noContent().build();
    }
}
