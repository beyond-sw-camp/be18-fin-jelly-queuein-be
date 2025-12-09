package com.beyond.qiin.domain.notification.service;

import com.beyond.qiin.domain.notification.entity.Notification;
import com.beyond.qiin.infra.event.reservation.ReservationCreatedPayload;
import com.beyond.qiin.infra.event.reservation.ReservationUpdatedPayload;

public interface NotificationCommandService {
    void notifyCreated(ReservationCreatedPayload payload);

    void notifyUpdated(ReservationUpdatedPayload payload);

    Notification makeCreateNotification(ReservationCreatedPayload payload);

    Notification makeUpdateNotification(ReservationUpdatedPayload payload);

    void markAsRead(Long notificationId, Long userId);

    void softDelete(Long notificationId, Long userId);

    void hardDelete(Long notificationId, Long userId);
}
