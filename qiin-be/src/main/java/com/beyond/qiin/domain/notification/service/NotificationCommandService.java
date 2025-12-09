package com.beyond.qiin.domain.notification.service;

import com.beyond.qiin.infra.event.reservation.ReservationEventPayload;

public interface NotificationCommandService {
    void notifyEvent(ReservationEventPayload payload);

    void markAsRead(Long notificationId, Long userId);

    void softDelete(Long notificationId, Long userId);

    void hardDelete(Long notificationId, Long userId);
}
