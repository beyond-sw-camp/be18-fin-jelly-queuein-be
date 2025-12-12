package com.beyond.qiin.domain.notification.dto;

import com.beyond.qiin.domain.notification.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationContext {
    private Long receiverId;
    private Long reservationId;
    private NotificationType type;
    private String json;
    private String startAt;
    private String endAt;
}
