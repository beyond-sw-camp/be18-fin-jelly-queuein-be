package com.beyond.qiin.domain.notification.dto;

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

    public static NotificationSseDto of(
            Long notificationId, String title, String message, String type, Instant createdAt) {
        return NotificationSseDto.builder()
                .notificationId(notificationId)
                .title(title)
                .message(message)
                .type(type)
                .createdAt(createdAt)
                .build();
    }
}
