package com.beyond.qiin.domain.booking.service.command;
import static org.mockito.Mockito.when;

import com.beyond.qiin.domain.booking.entity.Reservation;
import com.beyond.qiin.domain.booking.support.ReservationReader;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.assertj.core.api.Assertions.assertThat;

public class ReservationTimeValidationTest {


  private ReservationReader reservationReader;
  private ReservationCommandServiceImpl reservationService;

  @BeforeEach
  void setUp() {
    reservationReader = Mockito.mock(ReservationReader.class);
  }

  private Reservation res(String start, String end, Long id) {
    return Reservation.builder()
        .startAt(Instant.parse(start))
        .endAt(Instant.parse(end))
        .build();
  }

  private boolean check(String start, String end, List<Reservation> existing) {
    when(reservationReader.getActiveReservationsByAssetId(1L)).thenReturn(existing);
    return reservationService.isReservationTimeAvailable(
        null,
        1L,
        Instant.parse(start),
        Instant.parse(end)
    );
  }

  // 1) 완전 비겹침 - 왼쪽
  @Test
  void left_non_overlap() {
    List<Reservation> existing = List.of(
        res("2025-01-03T09:00:00Z", "2025-01-03T10:00:00Z", 1L)
    );

    boolean ok = check("2025-01-03T07:00:00Z", "2025-01-03T08:00:00Z", existing);

    assertThat(ok).isTrue();
  }

  // 2) 완전 비겹침 - 오른쪽
  @Test
  void right_non_overlap() {
    List<Reservation> existing = List.of(
        res("2025-01-03T09:00:00Z", "2025-01-03T10:00:00Z", 1L)
    );

    boolean ok = check("2025-01-03T11:00:00Z", "2025-01-03T12:00:00Z", existing);

    assertThat(ok).isTrue();
  }

  // 3) 끝-시작 맞닿음 허용
  @Test
  void touching_is_allowed() {
    List<Reservation> existing = List.of(
        res("2025-01-03T09:00:00Z", "2025-01-03T10:00:00Z", 1L)
    );

    // 신규 start == 기존 end
    boolean ok = check("2025-01-03T10:00:00Z", "2025-01-03T11:00:00Z", existing);

    assertThat(ok).isTrue();
  }

  // 4) 부분 겹침(왼쪽)
  @Test
  void partial_overlap_left() {
    List<Reservation> existing = List.of(
        res("2025-01-03T09:00:00Z", "2025-01-03T10:00:00Z", 1L)
    );

    boolean ok = check("2025-01-03T08:00:00Z", "2025-01-03T09:30:00Z", existing);

    assertThat(ok).isFalse();
  }

  // 5) 신규가 기존을 포함하는 경우
  @Test
  void new_includes_existing() {
    List<Reservation> existing = List.of(
        res("2025-01-03T09:00:00Z", "2025-01-03T10:00:00Z", 1L)
    );

    boolean ok = check("2025-01-03T08:00:00Z", "2025-01-03T12:00:00Z", existing);

    assertThat(ok).isFalse();
  }

  // 6) 신규가 기존 안쪽에 포함되는 경우
  @Test
  void new_inside_existing() {
    List<Reservation> existing = List.of(
        res("2025-01-03T09:00:00Z", "2025-01-03T10:00:00Z", 1L)
    );

    boolean ok = check("2025-01-03T09:30:00Z", "2025-01-03T09:45:00Z", existing);

    assertThat(ok).isFalse();
  }
}