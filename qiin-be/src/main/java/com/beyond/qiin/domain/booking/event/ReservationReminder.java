// package com.beyond.qiin.domain.booking.event;
//
// import com.beyond.qiin.domain.booking.entity.Reservation;
// import com.beyond.qiin.domain.booking.support.ReservationReader;
// import com.beyond.qiin.domain.notification.entity.Notification;
// import com.beyond.qiin.domain.notification.support.NotificationWriter;
// import java.time.Instant;
// import java.util.ArrayList;
// import java.util.List;
// import lombok.RequiredArgsConstructor;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Component;
//
// @Component
// @RequiredArgsConstructor
// public class ReservationReminder {
//
     // TODO : 카프카로 변경

//    private final ReservationReader reservationReader;
//
//    private final NotificationWriter notificationWriter;
//
//    @Scheduled(fixedDelay = 5000)
//    public void sendUpcomingReservationNotifications() {
//        Instant now = Instant.now();
//        Instant reminderTime = now.plusSeconds(600); // 10분 전 알림
//
//        // 예약 조회: 지금 시간 ~ 10분 뒤까지 == 시작 10분전인 알림들(now를 주지 않으면 의도하지 않은 과거 알림까지 가져올 수 있음)
//        List<Reservation> upcomingReservations = reservationReader.findReservationsStartingBetween(now, reminderTime);
//
//        for (Reservation reservation : upcomingReservations) {
//            List<Long> userIds = new ArrayList<>();
//            userIds.add(reservation.getApplicant().getId());
//            userIds.addAll(reservation.getAttendants().stream()
//                    .map(a -> a.getUser().getId())
//                    .toList());
//
//            for (Long userId : userIds) {
//                // TODO : send가 writer 안으로 들어가는 건 아닌 거 같은데
//                // 어쨋든 이 component에서 적용하려면
//                notificationWriter.sendNotification(
//                        userId,
//                        Notification.create(
//                                userId,
//                                reservation.getId(),
//                                NotificationType.RESERVATION_UPCOMING,
//                                "예약이 곧 시작됩니다: " + reservation.getStartAt(),
//                                "{}"));
//            }
//        }
//    }
// }
