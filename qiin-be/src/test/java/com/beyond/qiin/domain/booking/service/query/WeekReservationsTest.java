package com.beyond.qiin.domain.booking.service.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.beyond.qiin.domain.booking.dto.reservation.response.week_reservation.WeekReservationListResponseDto;
import com.beyond.qiin.domain.booking.entity.Reservation;
import com.beyond.qiin.domain.booking.support.ReservationReader;
import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.support.user.UserReader;
import com.beyond.qiin.domain.inventory.entity.Asset;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class WeekReservationsTest {
  @InjectMocks
  private ReservationQueryServiceImpl reservationQueryService;

  @Mock
  private UserReader userReader;

  @Mock
  private ReservationReader reservationReader;

  @Test
  void getWeeklyReservations_groupsByDayCorrectly() {
    Long userId = 1L;
    LocalDate date = LocalDate.of(2025, 12, 4); // 목요일

    ZoneId zone = ZoneId.of("Asia/Seoul");
    LocalDate monday = date.with(DayOfWeek.MONDAY);
    User user = User.builder()
        .userName("A")
        .email("A@gmail.com")
        .build();
    // Mock userReader
    when(userReader.findById(userId)).thenReturn(user);

    // Mock reservations
    Asset asset = Asset.builder()
        .name("Projector")
        .build();

    Reservation r1 = Reservation.builder()
        .asset(asset)
        .startAt(monday.atTime(9, 0).atZone(zone).toInstant())
        .endAt(monday.atTime(10, 0).atZone(zone).toInstant())
        .build();

    Reservation r2 = Reservation.builder()
        .asset(asset)
        .startAt(monday.plusDays(2).atTime(14, 0).atZone(zone).toInstant())
        .endAt(monday.plusDays(2).atTime(15, 0).atZone(zone).toInstant())
        .build();



    List<Reservation> reservations = new ArrayList<>();
    reservations.add(r1);
    reservations.add(r2);
    when(reservationReader.getReservationsByUserAndWeek(anyLong(), any(), any()))
        .thenReturn(reservations);

    // 실행
    WeekReservationListResponseDto result = reservationQueryService.getWeeklyReservations(userId, date);

    // 검증
    assertEquals(7, result.getReservations().size()); // 월~일 7일
    assertEquals(1, result.getReservations().get(0).getReservations().size()); // 월요일 예약 1개
    assertEquals(0, result.getReservations().get(1).getReservations().size()); // 화요일 예약 없음
    assertEquals(1, result.getReservations().get(2).getReservations().size()); // 수요일 예약 1개
  }
}
