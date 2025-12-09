package com.beyond.qiin.domain.notification.dto;

import com.beyond.qiin.domain.notification.entity.Notification;
import com.beyond.qiin.domain.notification.enums.NotificationStatus;
import com.beyond.qiin.domain.notification.enums.NotificationType;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class NotificationResponseDto {

    private Long notificationId;

    private Long reservationId; // aggregateId

    private String type; // NotificationType 이름

    private String status; // NotificationStatus 이름

    private String message;

    private boolean isRead;

    private Instant createdAt;

    private Instant deliveredAt;

    private Instant readAt;

    public static NotificationResponseDto from(Notification n) {

        return NotificationResponseDto.builder()
                .notificationId(n.getId())
                .reservationId(n.getAggregateId())
                .type(NotificationType.from(n.getType()).name())
                .status(NotificationStatus.from(n.getStatus()).name())
                .message(n.getMessage())
                .isRead(n.isRead())
                .createdAt(n.getCreatedAt())
                .deliveredAt(n.getDeliveredAt())
                .readAt(n.getReadAt())
                .build();
    }
}
