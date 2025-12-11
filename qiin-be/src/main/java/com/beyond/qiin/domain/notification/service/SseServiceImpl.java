package com.beyond.qiin.domain.notification.service;

import com.beyond.qiin.domain.notification.dto.NotificationSseDto;
import com.beyond.qiin.domain.notification.entity.Notification;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RequiredArgsConstructor
@Service
public class SseServiceImpl implements SseService {
    private static final Long TIMEOUT = 60L * 60 * 1000; // 1시간
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Override
    public SseEmitter connect(final Long userId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);

        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError(e -> emitters.remove(userId));

        // 연결 성공 이벤트
        sendConnectEvent(userId);
        log.info("SSE SEND TRY → userId={}, emitter exists={}", userId, emitters.containsKey(userId));

        return emitter;
    }

    @Override
    public void send(final Long userId, final Notification notification) { // db 조회 -> 병목 현상 가능하므로 entity 전체 보냄
        SseEmitter emitter = emitters.get(userId);
        if (emitter == null) return;

        try {
            NotificationSseDto dto = NotificationSseDto.of(notification);
            emitter.send(SseEmitter.event()
                    .name("NOTIFICATION") // sse event 이름
                    .data(dto));
        } catch (Exception e) {
            emitters.remove(userId);
        }
    }

    @Override
    public void sendConnectEvent(final Long userId) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter == null) return;

        try {
            emitter.send(SseEmitter.event().name("CONNECT").data("connected"));
        } catch (Exception e) {
            emitters.remove(userId);
        }
    }

    @Override
    public void disconnect(final Long userId) {
        SseEmitter emitter = emitters.remove(userId);
        if (emitter != null) {
            emitter.complete();
        }
    }
}
