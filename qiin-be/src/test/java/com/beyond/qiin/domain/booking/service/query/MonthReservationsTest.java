package com.beyond.qiin.domain.booking.service.query;

import static com.beyond.qiin.domain.iam.entity.QUser.user;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.beyond.qiin.domain.booking.dto.reservation.response.month_reservation.MonthReservationListResponseDto;
import com.beyond.qiin.domain.booking.entity.Reservation;
import com.beyond.qiin.domain.booking.support.ReservationReader;
import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.support.user.UserReader;
import com.beyond.qiin.domain.inventory.entity.Asset;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MonthReservationsTest {
  @InjectMocks
  private ReservationQueryServiceImpl reservationQueryService;

  @Mock
  private UserReader userReader;

  @Mock
  private ReservationReader reservationReader;

  @Test
  void getMonthlyReservations_returnsDailyDtos() {
    Asset asset = Asset.builder()
        .name("Projector")
        .build();

    User user = User.builder()
        .userName("A")
        .email("A@gmail.com")
        .build();
    // 테스트 내에서 zone 정의
    ZoneId zone = ZoneId.of("Asia/Seoul");
    YearMonth yearMonth = YearMonth.of(2025, 12);
    // 테스트 내에서 월의 첫째 날 기준 날짜
    LocalDate startOfMonth = yearMonth.atDay(1);

    // r1: 4일 예약
    Reservation r1 = Reservation.builder()
        .asset(asset)
        .startAt(startOfMonth.plusDays(3).atTime(9, 0).atZone(zone).toInstant()) // 1일 + 3 = 4일
        .endAt(startOfMonth.plusDays(3).atTime(10, 0).atZone(zone).toInstant())
        .build();

    // r2: 7일 예약
    Reservation r2 = Reservation.builder()
        .asset(asset)
        .startAt(startOfMonth.plusDays(6).atTime(14, 0).atZone(zone).toInstant()) // 1일 + 6 = 7일
        .endAt(startOfMonth.plusDays(6).atTime(15, 0).atZone(zone).toInstant())
        .build();
    Long userId = 1L;


    // Mock userReader
    when(userReader.findById(userId)).thenReturn(user);

    // Mock reservations 반환
    List<Reservation> reservations = List.of(r1, r2);
    Instant from = yearMonth.atDay(1).atStartOfDay(zone).toInstant();
    Instant to = yearMonth.atEndOfMonth().plusDays(1).atStartOfDay(zone).toInstant();

    when(reservationReader.getReservationsByUserAndYearMonth(userId, from, to))
        .thenReturn(reservations);

    // 실행
    MonthReservationListResponseDto result =
        reservationQueryService.getMonthlyReservations(userId, yearMonth);

    // 검증
    assertEquals(yearMonth.lengthOfMonth(), result.getReservations().size()); // 달의 모든 일
    assertEquals(1, result.getReservations().get(3).getReservations().size()); // 4일 예약 1개
    assertEquals(1, result.getReservations().get(6).getReservations().size()); // 6일 예약 1개
    assertEquals(0, result.getReservations().get(0).getReservations().size()); // 1일 예약 없음
  }
}
