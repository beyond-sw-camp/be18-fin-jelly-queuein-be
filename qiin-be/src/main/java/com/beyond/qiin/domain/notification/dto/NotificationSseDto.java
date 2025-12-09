package com.beyond.qiin.domain.notification.dto;

import com.beyond.qiin.domain.notification.entity.Notification;
import com.beyond.qiin.domain.notification.enums.NotificationType;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class NotificationSseDto {
    private Long notificationId;
    private String title;
    private String message;
    private String type; // status는 불필요(pending, sent, failed)
    private Instant createdAt;

    // TODO : id가 결합도 줄여주는 것은 맞으나 필드가 많아 엔티티로 대체
    public static NotificationSseDto of(Notification notification) {
        return NotificationSseDto.builder()
                .notificationId(notification.getId())
                .message(notification.getMessage())
                .type(NotificationType.from(notification.getType()).name())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
