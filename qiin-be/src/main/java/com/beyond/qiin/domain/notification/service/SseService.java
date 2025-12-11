package com.beyond.qiin.domain.notification.service;

import com.beyond.qiin.domain.notification.entity.Notification;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseService {
    SseEmitter connect(final Long userId);

    void sendConnectEvent(final Long userId);

    void disconnect(final Long userId);

    void send(final Long userId, final Notification notification);
}
