package com.beyond.qiin.domain.alarm.repository;

import com.beyond.qiin.domain.alarm.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationJpaRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByReceiverId(Long receiverId, Pageable pageable);
}
