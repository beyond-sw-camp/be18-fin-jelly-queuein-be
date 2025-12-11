package com.beyond.qiin.domain.notification.support;

import com.beyond.qiin.domain.notification.entity.Notification;
import com.beyond.qiin.domain.notification.repository.NotificationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class NotificationReader {
    private final NotificationJpaRepository notificationJpaRepository;

    @Transactional(readOnly = true)
    public Page<Notification> getNotifications(final Long userId, final Pageable pageable) {
        return notificationJpaRepository.findByReceiverId(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Notification getNotification(final Long notificationId) {
        return notificationJpaRepository
                .findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
    }
}
