package com.beyond.qiin.domain.notification.service;

import com.beyond.qiin.infra.event.reservation.ReservationEventPayload;

public interface NotificationCommandService {
    void notifyEvent(final ReservationEventPayload payload);

    void markAsRead(final Long notificationId, final Long userId);

    void softDelete(final Long notificationId, final Long userId);

    void hardDelete(final Long notificationId, final Long userId);
}
