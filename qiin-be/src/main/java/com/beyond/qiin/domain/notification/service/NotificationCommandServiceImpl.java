// package com.beyond.qiin.domain.notification.service;
//
// import com.beyond.qiin.domain.notification.entity.Notification;
// import com.beyond.qiin.domain.notification.enums.NotificationStatus;
// import com.beyond.qiin.domain.notification.enums.NotificationType;
// import com.beyond.qiin.domain.notification.exception.NotificationErrorCode;
// import com.beyond.qiin.domain.notification.exception.NotificationException;
// import com.beyond.qiin.domain.notification.repository.NotificationJpaRepository;
// import com.beyond.qiin.infra.event.reservation.ReservationCreatedPayload;
// import com.beyond.qiin.infra.event.reservation.ReservationUpdatedPayload;
// import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.databind.ObjectMapper;
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
//   private final ObjectMapper objectMapper;
//
//  @Override
//  @Transactional
//  public void markAsRead(Long notificationId, Long userId) {
//
//    Notification notification = notificationJpaRepository
//        .findByIdAndUserId(notificationId, userId)
//        .orElseThrow(() -> new NotificationException(NotificationErrorCode.NOTIFICATION_NOT_FOUND);
//
//    notification.markAsRead(); //isRead, readAt
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
//    notificationJpaRepository.deleteById(notificationId); //실제 delete
//  }
//
//
//  //각 crud 행위에 따른 메서드 구분
//  @Transactional
//   public void notifyCreated(ReservationCreatedPayload payload) {
//     Notification notification = createNotification(payload);
//
//     sseService.send(payload.getApplicantId(), notification);
//     notification.markDelivered();
//
//  }
//
//   @Transactional
//   public void notifyUpdated(ReservationUpdatedPayload payload) {
//
//    Notification notification = createUpdatedNotification(payload);
//
//     sseService.send(payload.getApplicantId(), notification);
//     notification.markDelivered();
//   }
//
//   //실제 알림 생성(예약 생성된 용도)
//   private Notification createNotification(ReservationCreatedPayload payload) {
//    String payloadJson;
//     try {
//       payloadJson = objectMapper.writeValueAsString(payload);
//     } catch (JsonProcessingException e) {
//       throw new RuntimeException("Notification payload 직렬화 실패", e);
//     }
//
//     //notification type 지정
//     NotificationType type = NotificationType.RESERVATION_CREATED;
//
//     String title = "예약 알림";
//
//     //메시지 생성
//     String message = type.formatMessage(payload.getStartAt(), payload.getEndAt());
//       Notification notification = Notification.create(
//           payload.getApplicantId(),
//           payload.getReservationId(),
//           NotificationType.RESERVATION_CREATED,
//           title,
//           message,
//           payloadJson,
//           NotificationStatus.PENDING
//       );
//       notificationJpaRepository.save(notification);
//
//       return notification;
//
//   }
//
//   private Notification createUpdateNotification(ReservationUpdatedPayload payload) {
//     String payloadJson;
//     try {
//       payloadJson = objectMapper.writeValueAsString(payload);
//     } catch (JsonProcessingException e) {
//       throw new RuntimeException("Notification payload 직렬화 실패", e);
//     }
//
//     //TODO : notification type 지정
//     NotificationType type = NotificationType.RESERVATION_CREATED;
//
//     //TODO : type 별 분리
//     String title = "예약 알림";
//
//     //메시지 생성
//     String message = type.formatMessage(payload.getStartAt(), payload.getEndAt());
//     Notification notification = Notification.create(
//         payload.getApplicantId(),
//         payload.getReservationId(),
//         notificationType,
//         title,
//         message,
//         payloadJson,
//         notificationStatus
//     );
//     notificationJpaRepository.save(notification);
//
//     return notification;
//
//   }
//
//   //TODO : payload 통일
//   //status별로 notification 생성하도록 메서드 일원화
//   //생성 시 관련자들에게 invite 가도록만 추가
//   //created 자체는 그냥 approved이긴 함(생성한 건 알림을 주지 말까?)
//   public Notification createUpdatedNotification(String reservationStatus) {
//     NotificationType type = switch (reservationStatus) {
//       case "APPROVED" -> NotificationType.RESERVATION_APPROVED;
//       case "REJECTED" -> NotificationType.RESERVATION_REJECTED;
//       case "UNAVAILABLE" -> NotificationType.RESERVATION_UNAVAILABLE;
//       default -> throw new NotificationException(
//           NotificationErrorCode.NOTIFICATION_NOT_FOUND,
//           "Unknown reservation status: "
//       );
//     };
//
//     return createUpNotification(payload, type);
//   }
//
//
//
// }
