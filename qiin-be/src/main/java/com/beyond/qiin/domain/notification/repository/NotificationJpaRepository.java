package com.beyond.qiin.domain.notification.repository;

import com.beyond.qiin.domain.notification.entity.Notification;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationJpaRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiverId(Long receiverId);

    Optional<Notification> findByIdAndUserId(Long notificationId, Long userId);
}
