package com.beyond.qiin.domain.alarm.dto;

import com.beyond.qiin.domain.alarm.enums.NotificationType;
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
