package com.beyond.qiin.domain.booking.event;

import com.beyond.qiin.domain.alarm.entity.OutboxEvent;
import com.beyond.qiin.domain.alarm.support.OutboxEventWriter;
import com.beyond.qiin.domain.booking.entity.Reservation;
import com.beyond.qiin.infra.event.reservation.ReservationEventPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationExternalEventPublisher {
    private final OutboxEventWriter outboxEventWriter;
    private final ObjectMapper objectMapper; // 객체 -> string용

    public void publishEventCreated(Reservation reservation, List<Long> attendantUserIds) {
        ReservationEventPayload payload = ReservationEventPayload.from(reservation, attendantUserIds);
        publish("reservation-event", payload, reservation.getId()); // topic의 key
    }

    private void publish(String eventType, Object payload, Long aggregateId) {
        try {
            String payloadJson = objectMapper.writeValueAsString(payload);

            OutboxEvent event = OutboxEvent.create(eventType, payloadJson, aggregateId, "reservation");

            outboxEventWriter.save(event);

        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize payload for eventType=" + eventType, e);
        }
    }
}
