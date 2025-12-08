package com.beyond.qiin.domain.notification;

import com.beyond.qiin.domain.notification.entity.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationJpaRepository extends JpaRepository<Notification,Long> {
  List<Notification> findByReceiverId(Long receiverId);
}
