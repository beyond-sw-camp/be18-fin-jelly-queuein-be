//package com.beyond.qiin.domain.notification.service;
//
//import com.beyond.qiin.domain.notification.NotificationJpaRepository;
//import com.beyond.qiin.domain.notification.entity.Notification;
//import com.beyond.qiin.domain.notification.exception.NotificationErrorCode;
//import com.beyond.qiin.infra.event.reservation.ReservationCreatedPayload;
//import com.beyond.qiin.infra.event.reservation.ReservationUpdatedPayload;
//import java.time.Instant;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@RequiredArgsConstructor
//@Service
//public class NotificationCommandServiceImpl implements NotificationCommandService {
//
//  private final NotificationJpaRepository notificationJpaRepository;
//
//  @Override
//  @Transactional
//  public Notification notifyCreated(ReservationCreatedPayload payload) {
//
//    // 도메인 규칙 적용
//    Notification notification = Notification.create();
//
//    return notification;
//  }
//
//  @Override
//  @Transactional
//  public Notification notifyUpdated(ReservationUpdatedPayload payload) {
//
//    // 도메인 규칙 적용
//
//
//    return notification;
//  }
//
//  @Override
//  @Transactional
//  public void markAsRead(Long notificationId, Long userId) {
//
//    Notification notification = notificationJpaRepository
//        .findByIdAndUserId(notificationId, userId)
//        .orElseThrow(() -> new NotificationErrorCode.NOTIFICATION_NOT_FOUND);
//
//    notification.markAsRead();  // 도메인 메서드
//
//    notificationJpaRepository.save(notification);
//  }
//
//  @Override
//  @Transactional
//  public void softDelete(Long notificationId, Long userId){
//    //
//
//  }
//
//  @Override
//  @Transactional
//  public void hardDelete(Long notificationId, Long userId){
//
//    notificationJpaRepository.deleteById(notificationId);
//  }
//
//
//}
