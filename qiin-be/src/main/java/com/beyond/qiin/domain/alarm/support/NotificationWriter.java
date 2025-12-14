package com.beyond.qiin.domain.alarm.support;

import com.beyond.qiin.domain.alarm.repository.NotificationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationWriter {
    private final NotificationJpaRepository notificationJpaRepository;
}
