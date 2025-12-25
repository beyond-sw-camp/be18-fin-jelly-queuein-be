package com.beyond.qiin.domain.alarm.dto;

import com.beyond.qiin.domain.alarm.entity.Notification;
import com.beyond.qiin.domain.alarm.enums.NotificationType;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class NotificationSseDto {
    private final Long notificationId;
    private final String title;
    private final String message;
    private String type; // status는 불필요(pending, sent, failed)
    private final Instant createdAt;

    // TODO : id가 결합도 줄여주는 것은 맞으나 필드가 많아 엔티티로 대체
    public static NotificationSseDto of(final Notification notification) {
        return NotificationSseDto.builder()
                .notificationId(notification.getId())
                .message(notification.getMessage())
                .type(NotificationType.from(notification.getType()).name())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
