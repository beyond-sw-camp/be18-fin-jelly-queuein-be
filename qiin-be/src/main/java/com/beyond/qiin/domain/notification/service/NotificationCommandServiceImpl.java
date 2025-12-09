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
//  //선착순 생성 시
//  @Override
//  @Transactional
//  public Notification notifyCreated(ReservationCreatedPayload payload) {
//
//    Long userId = payload.getApplicantId();
//    Long reservationId = payload.getReservationId();
//
//    String title = "예약이 생성되었습니다";
//
//    String message = String.format(
//        "자원(ID: %d)의 예약이 생성되었습니다.\n시간: %s ~ %s\n상태: %s",
//        payload.getReservationId(),
//        payload.getAssetId(),
//        payload.getStartAt(),
//        payload.getEndAt(),
//        payload.getStatus()
//    );
//
//    String payloadJson = toJson(payload); // ObjectMapper 사용
//
//    Notification notification = Notification.create(
//        userId,
//        reservationId,
//        NotificationType.RESERVATION_CREATED, //TODO : payload.status 에 따른 분기
//        title,
//        message,
//        payloadJson,
//        NotificationStatus.PENDING
//    );
//
//    notificationJpaRepository.save(notification);
//
//    sseService.send(userId, notification.getId(), title, message, NotificationType.RESERVATION_CREATED.name(),
// notification.getCreatedAt());
//    return notification;
//  }
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
//   public Notification notifyReservationCreated(ReservationUpdatedPayload payload) {
//     return createReservationNotification(
//         userId,
//         aggregateId,
//         payload,
//         NotificationType.RESERVATION_CREATED
//     );
//   }
//
//   public Notification notifyReservationInvited(ReservationUpdatedPayload payload) {
//     return createReservationNotification(
//         userId,
//         aggregateId,
//         payload,
//         NotificationType.RESERVATION_INVITED
//     );
//   }
//
//   public Notification notifyReservation10MinBefore(ReservationUpdatedPayload payload) {
//     return createReservationNotification(
//         userId,
//         aggregateId,
//         payload,
//         NotificationType.RESERVATION_10_MIN_BEFORE
//     );
//   }
//
//
//
//   public Notification createReservationNotification(
//       Long userId,
//       Long aggregateId,
//       ReservationUpdatedPayload payload,
//       NotificationType type
//   ) {
//     String message = type.formatMessage(
//         payload.getStartAt(),
//         payload.getEndAt()
//     );
//
//     String payloadJson = createPayloadByType(type, payload);
//
//     return Notification.create(
//         userId,
//         aggregateId,
//         NotificationStatus.PENDING,
//         type.name(),   // title 통일
//         message,
//         payloadJson,
//         type
//     );
//   }
//
//   //TODO : payload 통일
//   //status별로 notification 생성하도록 메서드 일원화
//   //생성 시 관련자들에게 invite 가도록만 추가
//   //created 자체는 그냥 approved이긴 함(생성한 건 알림을 주지 말까?)
//   private String convertStatusToNotificationType(String status) {
//     return switch (status) {
//       case "APPROVED" -> NotificationType.RESERVATION_APPROVED;
//       case "REJECTED" -> NotificationType.RESERVATION_REJECTED;
//       case "UNAVAILABLE" -> NotificationType.RESERVATION_UNAVAILABLE;
//
//       case "RESERVATION_INVITED" -> NotificationType.RESERVATION_INVITED;
//       case "RESERVATION_PLANNED" -> NotificationType.RESERVATION_PLANNED;
//
//       case "CREATED" -> NotificationType.RESERVATION_CREATED;
//     };
//   }
//
//
//
//
// }
