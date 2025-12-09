package com.beyond.qiin.domain.notification.support;

import com.beyond.qiin.domain.notification.entity.Notification;
import com.beyond.qiin.domain.notification.repository.NotificationJpaRepository;
import java.util.List;
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
    public Page<Notification> getNotifications(Long userId, Pageable pageable) {
        return notificationJpaRepository.findByReceiverId(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Notification getNotification(Long notificationId) {
        return notificationJpaRepository
                .findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
    }
}
