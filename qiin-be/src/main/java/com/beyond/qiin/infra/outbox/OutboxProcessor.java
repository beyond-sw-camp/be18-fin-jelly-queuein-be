package com.beyond.qiin.infra.outbox;

import com.beyond.qiin.domain.outbox.entity.OutboxEvent;
import com.beyond.qiin.domain.outbox.support.OutboxEventWriter;
import com.beyond.qiin.infra.kafka.KafkaProducerService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxProcessor { // 생성 / 수정으로 인해 outbox 생성 시 payload을 카프카 서버로 송신

    private final OutboxEventWriter outboxEventWriter;
    private final KafkaProducerService kafkaProducerService;

    @Scheduled(fixedDelay = 5000) // 5초마다 실행 - polling 방식으로 outbox 저장 확인
    public void processOutboxEvents() {
        List<OutboxEvent> events = outboxEventWriter.findUnpublishedEvents();

        for (OutboxEvent event : events) {
            try { // 해당 토픽명으로 페이로드를 보냄
                kafkaProducerService.sendMessage(event.getEventType(), event.getPayload());
                event.markPublished(); // 상태 업데이트
                outboxEventWriter.save(event);
                log.info("sent event from outbox processor: {}", event);
            } catch (Exception e) {
                log.error("event publish failed: id={}, {}", event.getId(), e.getMessage());
                event.markFailed(); // 실패 상태로 변경 (필요 시)
                outboxEventWriter.save(event);
            }
        }
    }
}
