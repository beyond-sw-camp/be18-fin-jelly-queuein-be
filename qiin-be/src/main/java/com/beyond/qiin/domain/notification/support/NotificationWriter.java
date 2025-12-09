package com.beyond.qiin.domain.notification.support;

import com.beyond.qiin.domain.notification.repository.NotificationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationWriter {
    private final NotificationJpaRepository notificationJpaRepository;
}
