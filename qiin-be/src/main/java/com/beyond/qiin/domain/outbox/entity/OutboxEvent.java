package com.beyond.qiin.domain.outbox.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
// @Entity //TODO : flyway 추가 후
@Table(
        name = "outbox",
        indexes = {
            @Index(name = "idx_outbox_published", columnList = "is_published"),
            @Index(name = "idx_outbox_aggregate", columnList = "aggregate_id, aggregate_type")
        })
public class OutboxEvent { // outbox(table) : 저장소, outbox event : 이벤트 한건

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "outbox_id", columnDefinition = "BINARY(16)", updatable = false, nullable = false) // 16이 더 성능이 좋음
    private UUID id; // Outbox PK (UUID)

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType; // ex: reservation-created

    @Lob
    @Column(name = "payload", nullable = false, columnDefinition = "JSON")
    private String payload; // 이벤트 JSON 데이터

    @Column(name = "is_published", nullable = false)
    private boolean isPublished; // Kafka 발행 여부 (보통 status로 대체 가능)

    @Column(name = "aggregate_id", nullable = false, columnDefinition = "bigint")
    private Long aggregateId; // ex: reservation id

    @Column(name = "aggregate_type", nullable = false, length = 100)
    private String aggregateType; // ex: reservation

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP(6)")
    private Instant createdAt;

    public static OutboxEvent create(String eventType, String payload, Long aggregateId, String aggregateType) {

        return OutboxEvent.builder()
                .eventType(eventType)
                .payload(payload)
                .aggregateId(aggregateId)
                .aggregateType(aggregateType)
                .isPublished(false)
                .createdAt(Instant.now())
                .build();
    }

    public void markPublished() {
        this.isPublished = true;
    }

    public void markFailed() {
        this.isPublished = false;
    }
}
