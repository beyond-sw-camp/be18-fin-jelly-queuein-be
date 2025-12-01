package com.beyond.qiin.domain.booking.event;

import com.beyond.qiin.domain.booking.entity.Reservation;
import com.beyond.qiin.domain.outbox.entity.OutboxEvent;
import com.beyond.qiin.domain.outbox.support.OutboxEventWriter;
import com.beyond.qiin.infra.event.reservation.ReservationCreatedPayload;
import com.beyond.qiin.infra.event.reservation.ReservationUpdatedPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class ReservationEventPublisher {
  private final OutboxEventWriter outboxEventWriter;
  private final ObjectMapper objectMapper; // 객체 -> string용

  public void publishCreated(Reservation reservation) {
    ReservationCreatedPayload payload = ReservationCreatedPayload.from(reservation);
    publish("reservation-created", payload, reservation.getId()); // topic의 key
  }

  public void publishUpdated(Reservation reservation) {
    ReservationUpdatedPayload payload = ReservationUpdatedPayload.from(reservation);
    publish("reservation-updated", payload, reservation.getId());
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

