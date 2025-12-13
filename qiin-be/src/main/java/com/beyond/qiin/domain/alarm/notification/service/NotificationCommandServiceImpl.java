package com.beyond.qiin.domain.alarm.notification.service;

import com.beyond.qiin.domain.alarm.notification.dto.NotificationContext;
import com.beyond.qiin.domain.alarm.notification.entity.Notification;
import com.beyond.qiin.domain.alarm.notification.enums.NotificationType;
import com.beyond.qiin.domain.alarm.notification.exception.NotificationErrorCode;
import com.beyond.qiin.domain.alarm.notification.exception.NotificationException;
import com.beyond.qiin.domain.alarm.notification.repository.NotificationJpaRepository;
import com.beyond.qiin.infra.event.reservation.ReservationEventPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class NotificationCommandServiceImpl implements NotificationCommandService {

    private final NotificationJpaRepository notificationJpaRepository;
    private final SseService sseService;
    private final ObjectMapper objectMapper;

    // 각 crud 행위에 따른 메서드 구분
    // 참여자들을 payload으로 담게 되면 빠른 조회 가능(부정확) => 생성 시
    @Override
    @Transactional
    public void notifyEvent(final ReservationEventPayload payload) {

        String json = toJson(payload);

        NotificationType type = toNotificationType(payload.getStatus());
        // 신청자 알림
        sendNotification(NotificationContext.builder()
                .receiverId(payload.getApplicantId())
                .reservationId(payload.getReservationId())
                .type(type)
                .json(json)
                .startAt(payload.getStartAt())
                .endAt(payload.getEndAt())
                .build());

        // 예약 선착순 생성 시 or 예약 승인 시에만 참여자들에게도 알림
        if (type == NotificationType.RESERVATION_APPROVED || type == NotificationType.RESERVATION_CREATED) {
            notifyAttendants(payload, NotificationType.RESERVATION_INVITED, json);
        }
    }

    @Override
    @Transactional
    public void markAsRead(final Long notificationId, final Long userId) {

        Notification notification = notificationJpaRepository
                .findById(notificationId)
                .orElseThrow(() -> new NotificationException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));

        notification.markAsRead(); // isRead, readAt

        notificationJpaRepository.save(notification);
    }

    @Override
    @Transactional
    public void softDelete(final Long notificationId, final Long userId) {
        Notification notification = notificationJpaRepository
                .findById(notificationId)
                .orElseThrow(() -> new NotificationException(NotificationErrorCode.NOTIFICATION_NOT_FOUND)); // 예외 처리

        notification.delete();
    }

    @Override
    @Transactional
    public void hardDelete(final Long notificationId, final Long userId) {
        notificationJpaRepository.deleteById(notificationId); // 실제 delete
    }

    // TODO : 참여자들을 payload으로 담지 않으면 정확, 느림(검증 한번 더 하기 때문) => 수정 시 (근데 일단 통일)
    public NotificationType toNotificationType(String reservationStatus) {
        return switch (reservationStatus) {
            case "CREATED" -> NotificationType.RESERVATION_CREATED;
            case "APPROVED" -> NotificationType.RESERVATION_APPROVED;
            case "REJECTED" -> NotificationType.RESERVATION_REJECTED;
            case "UNAVAILABLE" -> NotificationType.RESERVATION_UNAVAILABLE;
            default -> throw new NotificationException(
                    NotificationErrorCode.NOTIFICATION_NOT_FOUND, "Unknown notification status: ");
        };
    }

    // TODO : 몇분전 알림은 별도 처리 (참여자들의 경우도 마찬가지) -> 스케줄러 활용

    // 헬퍼 메서드들
    private void notifyAttendants(final ReservationEventPayload payload, final NotificationType type, String json) {
        payload.getAttendantUserIds()
                .forEach(uid -> sendNotification(NotificationContext.builder()
                        .receiverId(uid)
                        .reservationId(payload.getReservationId())
                        .type(type)
                        .json(json)
                        .startAt(payload.getStartAt())
                        .endAt(payload.getEndAt())
                        .build()));
    }

    private String toJson(final Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Notification payload 직렬화 실패", e);
        }
    }

    private void sendNotification(final NotificationContext ctx) {

        Notification saved = createAndSaveNotification(ctx);
        sseService.send(ctx.getReceiverId(), saved);
        saved.markDelivered();
    }

    // 부모 메서드의 트랜잭션을 이어받으므로 Transactional 생략
    private Notification createAndSaveNotification(final NotificationContext ctx) {

        String message = ctx.getType().formatMessage(ctx.getStartAt(), ctx.getEndAt());

        Notification notification =
                Notification.create(ctx.getReceiverId(), ctx.getReservationId(), ctx.getType(), message, ctx.getJson());

        return notificationJpaRepository.save(notification);
    }
}
