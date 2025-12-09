package com.beyond.qiin.domain.notification.service;

import java.time.Instant;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseService {
    SseEmitter connect(Long userId);

    void disconnect(Long userId);

    void send(Long userId, Long notificationId, String title, String message, String type, Instant createdAt);
}
