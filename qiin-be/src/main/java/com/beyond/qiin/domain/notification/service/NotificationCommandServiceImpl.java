package com.beyond.qiin.domain.notification.service;

import com.beyond.qiin.domain.notification.entity.Notification;
import com.beyond.qiin.domain.notification.enums.NotificationType;
import com.beyond.qiin.domain.notification.exception.NotificationErrorCode;
import com.beyond.qiin.domain.notification.exception.NotificationException;
import com.beyond.qiin.domain.notification.repository.NotificationJpaRepository;
import com.beyond.qiin.infra.event.reservation.ReservationCreatedPayload;
import com.beyond.qiin.infra.event.reservation.ReservationUpdatedPayload;
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
    public void notifyCreated(ReservationCreatedPayload payload) {
        Notification notification = makeCreateNotification(payload);

        sendNotification(payload.getApplicantId(), notification);

        // 참여자들에게 알림 전송
        payload.getAttendantIds().forEach(userId -> {
            Notification inviteNotification = makeInviteNotification(payload, userId);
            sendNotification(userId, inviteNotification);
        });
    }

    @Override
    @Transactional
    public void notifyUpdated(ReservationUpdatedPayload payload) {

        Notification notification = makeUpdateNotification(payload);
        sendNotification(payload.getApplicantId(), notification);
    }

    private void sendNotification(Long receiverId, Notification notification) {
        sseService.send(receiverId, notification);
        notification.markDelivered();
    }

    // 실제 알림 생성(예약 생성된 용도)
    @Override
    public Notification makeCreateNotification(ReservationCreatedPayload payload) {
        String payloadJson;
        try {
            payloadJson = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Notification payload 직렬화 실패", e);
        }

        // notification type 지정
        NotificationType type = NotificationType.RESERVATION_CREATED;

        // 메시지 생성
        String message = type.formatMessage(payload.getStartAt(), payload.getEndAt());
        Notification notification = Notification.create(
                payload.getApplicantId(),
                payload.getReservationId(),
                NotificationType.RESERVATION_CREATED,
                message,
                payloadJson);
        notificationJpaRepository.save(notification);

        return notification;
    }

    // TODO : 실제 reservation의 attendant id들은 user id가 아니라 이 기준으로 전송을 할 수 업승ㅁ
    // payload안에 넣어주던가 아니면 여기서 가공 로직 거쳐서 실제 사용자들 적용해줘야함
    @Override
    public Notification makeInviteNotification(ReservationCreatedPayload payload, Long attendantId) {
        String payloadJson;
        try {
            payloadJson = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Notification payload 직렬화 실패", e);
        }

        // notification type 지정
        NotificationType type = NotificationType.RESERVATION_INVITED;

        // 메시지 생성
        String message = type.formatMessage(payload.getStartAt(), payload.getEndAt());
        Notification notification = Notification.create(
                attendantId, /// receiverId
                payload.getReservationId(),
                type,
                message,
                payloadJson);
        notificationJpaRepository.save(notification);

        return notification;
    }

    @Override
    public Notification makeUpdateNotification(ReservationUpdatedPayload payload) {
        String payloadJson;
        try {
            payloadJson = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Notification payload 직렬화 실패", e);
        }

        NotificationType type = toNotificationType(payload.getStatus());

        // 메시지 생성
        String message = type.formatMessage(payload.getStartAt(), payload.getEndAt());
        Notification notification =
                Notification.create(payload.getApplicantId(), payload.getReservationId(), type, message, payloadJson);
        notificationJpaRepository.save(notification);

        return notification;
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {

        Notification notification = notificationJpaRepository
                .findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new NotificationException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));

        notification.markAsRead(); // isRead, readAt

        notificationJpaRepository.save(notification);
    }

    @Override
    @Transactional
    public void softDelete(Long notificationId, Long userId) {
        Notification notification = notificationJpaRepository
                .findById(notificationId)
                .orElseThrow(() -> new NotificationException(NotificationErrorCode.NOTIFICATION_NOT_FOUND)); // 예외 처리

        notification.delete(userId);
    }

    @Override
    @Transactional
    public void hardDelete(Long notificationId, Long userId) {
        notificationJpaRepository.deleteById(notificationId); // 실제 delete
    }

    // TODO :
    // 생성 시 참여자들에게 invite 가도록만 추가 -> reservation 조회 -> 참여자 목록 조회(유효 검증)

    // 참여자들을 payload으로 담지 않으면 정확, 느림(검증 한번 더 하기 때문) => 수정 시
    public NotificationType toNotificationType(String reservationStatus) {
        NotificationType type =
                switch (reservationStatus) {
                    case "APPROVED" -> NotificationType.RESERVATION_APPROVED;
                    case "REJECTED" -> NotificationType.RESERVATION_REJECTED;
                    case "UNAVAILABLE" -> NotificationType.RESERVATION_UNAVAILABLE;
                    default -> throw new NotificationException(
                            NotificationErrorCode.NOTIFICATION_NOT_FOUND, "Unknown notification status: ");
                };

        return type;
    }

    // TODO : 몇분전 알림은 별도 처리 (참여자들의 경우도 마찬가지) -> 스케줄러 활용

}
