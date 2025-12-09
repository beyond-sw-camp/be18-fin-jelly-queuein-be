// package com.beyond.qiin.domain.notification.service;
//
// import static org.flywaydb.core.internal.util.JsonUtils.toJson;
//
// import com.beyond.qiin.domain.notification.repository.NotificationJpaRepository;
// import com.beyond.qiin.domain.notification.entity.Notification;
// import com.beyond.qiin.domain.notification.enums.NotificationStatus;
// import com.beyond.qiin.domain.notification.enums.NotificationType;
// import com.beyond.qiin.domain.notification.exception.NotificationErrorCode;
// import com.beyond.qiin.domain.notification.exception.NotificationException;
// import com.beyond.qiin.infra.event.reservation.ReservationCreatedPayload;
// import com.beyond.qiin.infra.event.reservation.ReservationPayload;
// import com.beyond.qiin.infra.event.reservation.ReservationUpdatedPayload;
// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
//
// @RequiredArgsConstructor
// @Service
// public class NotificationCommandServiceImpl implements NotificationCommandService {
//
//  private final NotificationJpaRepository notificationJpaRepository;
//  private final SseService sseService;
//
//  @Override
//  @Transactional
//  public void markAsRead(Long notificationId, Long userId) {
//
//    Notification notification = notificationJpaRepository
//        .findByIdAndUserId(notificationId, userId)
//        .orElseThrow(() -> new NotificationException(NotificationErrorCode.NOTIFICATION_NOT_FOUND);
//
//    notification.markAsRead();
//
//    notificationJpaRepository.save(notification);
//  }
//
//  @Override
//  @Transactional
//  public void softDelete(Long notificationId, Long userId){
//    Notification notification = notificationJpaRepository
//        .findById(notificationId)
//        .orElseThrow(() -> new NotificationException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));  // 예외 처리
//
//    notification.delete(userId);
//
//  }
//
//  @Override
//  @Transactional
//  public void hardDelete(Long notificationId, Long userId){
//    notificationJpaRepository.deleteById(notificationId);
//  }
//
//
//   public void notifyCreated(ReservationCreatedPayload payload) {
//     Notification notification = createNotification(payload, NotificationType.RESERVATION_CREATED);
//
//     sseService.send(userId, notification.getId(), title, message, NotificationType.RESERVATION_CREATED.name(),
//         notification.getCreatedAt());
//  }
//
//   public void notifyUpdated(ReservationUpdatedPayload payload) {
//     Notification notification = createUpdatedNotification(payload, NotificationType.RESERVATION_CREATED);
//
//     sseService.send(userId, notification.getId(), title, message, NotificationType.RESERVATION_CREATED.name(),
//         notification.getCreatedAt());
//   }
//
//   private Notification createNotification(Object payload, NotificationType type) {
//     String payloadJson = objectMapper.writeValueAsString(payload);
//     String message = type.formatMessage(payload.getStartAt(), payload.getEndAt());
//
//     Notification notification = Notification.create(
//         payload.getApplicantId(),
//         payload.getReservationId(),
//         type.name(),
//         message,
//         payloadJson,
//         NotificationStatus.PENDING
//     );
//     notificationJpaRepository.save(notification);
//
//     return notification;
//   }
//
//   //TODO : payload 통일
//   //status별로 notification 생성하도록 메서드 일원화
//   //생성 시 관련자들에게 invite 가도록만 추가
//   //created 자체는 그냥 approved이긴 함(생성한 건 알림을 주지 말까?)
//   public Notification createUpdatedNotification(ReservationUpdatedPayload payload) {
//     NotificationType type = switch (payload.getStatus()) {
//       case "APPROVED" -> NotificationType.RESERVATION_APPROVED;
//       case "REJECTED" -> NotificationType.RESERVATION_REJECTED;
//       case "UNAVAILABLE" -> NotificationType.RESERVATION_UNAVAILABLE;
//       default -> throw new NotificationException(
//           NotificationErrorCode.NOTIFICATION_NOT_FOUND,
//           "Unknown reservation status: " + payload.getStatus()
//       );
//     };
//
//     return createNotification(payload, type);
//   }
//
//
//
// }
