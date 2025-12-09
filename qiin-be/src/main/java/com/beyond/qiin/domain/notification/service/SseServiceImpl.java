package com.beyond.qiin.domain.notification.service;

import com.beyond.qiin.domain.notification.dto.NotificationSseDto;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@Service
public class SseServiceImpl implements SseService {
    private static final Long TIMEOUT = 60L * 60 * 1000; // 1시간

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Override
    public SseEmitter connect(Long userId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);

        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError(e -> emitters.remove(userId));

        // TODO : 연결 성공 이벤트
        send(userId, -1L, "연결됨", "SSE 연결 성공", NotificationType.CONNECT.name(), Instant.now());

        return emitter;
    }

    @Override
    public void disconnect(Long userId) {
        SseEmitter emitter = emitters.remove(userId);
        if (emitter != null) {
            emitter.complete();
        }
    }

    @Override
    public void send(Long userId, Long notificationId, String title, String message, String type, Instant createdAt) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter == null) return;

        try {
            NotificationSseDto dto = NotificationSseDto.of(notificationId, title, message, type, createdAt);
            emitter.send(SseEmitter.event()
                    .name("NOTIFICATION") // TODO
                    .data(dto));
        } catch (Exception e) {
            emitters.remove(userId);
        }
    }
}
