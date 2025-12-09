package com.beyond.qiin.domain.notification.service;

import com.beyond.qiin.domain.notification.entity.Notification;
import com.beyond.qiin.domain.notification.repository.NotificationJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class NotificationReader {
    private final NotificationJpaRepository notificationJpaRepository;

    @Transactional(readOnly = true)
    public List<Notification> getNotifications(Long userId) {
        return notificationJpaRepository.findByReceiverId(userId);
    }

    @Transactional(readOnly = true)
    public Notification getNotification(Long notificationId) {
        return notificationJpaRepository
                .findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
    }
}
