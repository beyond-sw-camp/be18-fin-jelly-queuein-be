package com.beyond.qiin.domain.alarm.dto;

import com.beyond.qiin.domain.alarm.entity.Notification;
import com.beyond.qiin.domain.alarm.enums.NotificationStatus;
import com.beyond.qiin.domain.alarm.enums.NotificationType;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class NotificationResponseDto {

    private final Long notificationId;

    private final Long reservationId; // aggregateId

    private final String type; // NotificationType 이름

    private final String status; // NotificationStatus 이름

    private final String message;

    private final boolean isRead;

    private final Instant createdAt;

    private final Instant deliveredAt;

    private final Instant readAt;

    public static NotificationResponseDto from(final Notification n) {

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
