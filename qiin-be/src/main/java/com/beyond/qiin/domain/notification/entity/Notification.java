package com.beyond.qiin.domain.notification.entity;

import com.beyond.qiin.domain.notification.enums.NotificationStatus;
import com.beyond.qiin.domain.notification.enums.NotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "notification")
@SQLRestriction("deleted_at is null")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    @Column(name = "aggregate_id", nullable = false)
    private Long aggregateId;

    @Column(name = "message", length = 2000, nullable = false)
    private String message;

    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private int status = 0; // pending

    @Transient
    private NotificationStatus notificationStatus;

    @Column(name = "type", nullable = false, length = 30)
    private int type;

    @Transient
    private NotificationType notificationType;

    @Lob // large object(긴 문자열)
    @Column(name = "payload", nullable = false, columnDefinition = "LONGTEXT")
    private String payload; // 메타데이터 보관용도(예약 자체에 관한 정보와 알림 엔티티를 분리)

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private boolean isRead = false;

    @Column(name = "delivered_at", columnDefinition = "TIMESTAMP(6)")
    private Instant deliveredAt;

    @Column(name = "read_at", columnDefinition = "TIMESTAMP(6)")
    private Instant readAt;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP(6)")
    private Instant createdAt;

    @Column(name = "deleted_at", nullable = false, columnDefinition = "TIMESTAMP(6)")
    private Instant deletedAt;

    @PrePersist
    public void prePersist() { // db 반영시점
        this.createdAt = Instant.now();
    }

    public void delete() {
        if (this.deletedAt != null) {
            return;
        }
        this.deletedAt = Instant.now();
    }

    public static Notification create(
            Long userId, Long aggregateId, NotificationType type, String message, String payloadJson) {
        return Notification.builder()
                .receiverId(userId)
                .aggregateId(aggregateId)
                .type(type.getCode())
                .message(message)
                .payload(payloadJson)
                .createdAt(Instant.now())
                .status(NotificationStatus.PENDING.getCode())
                .isRead(false)
                .build();
    }

    public void markDelivered() {
        this.deliveredAt = Instant.now();
        this.status = NotificationStatus.SENT.getCode();
    }

    public void markFailed() {
        this.status = NotificationStatus.FAILED.getCode();
    }

    public void markAsRead() {
        if (!this.isRead) {
            this.isRead = true;
            this.readAt = Instant.now();
        }
    }
}
