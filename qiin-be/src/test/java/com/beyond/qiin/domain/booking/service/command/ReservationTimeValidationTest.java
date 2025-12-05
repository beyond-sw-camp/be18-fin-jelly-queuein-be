package com.beyond.qiin.domain.booking.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.beyond.qiin.domain.booking.entity.Reservation;
import com.beyond.qiin.domain.booking.support.ReservationReader;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ReservationTimeValidationTest {

    @Mock
    ReservationReader reservationReader;

    @InjectMocks
    ReservationCommandServiceImpl reservationService;

    private Reservation res(String start, String end, Long id) {
        Reservation r = Reservation.builder()
                .startAt(Instant.parse(start))
                .endAt(Instant.parse(end))
                .build();

        ReflectionTestUtils.setField(r, "id", id);
        return r;
    }

    private boolean check(String start, String end, List<Reservation> existing) {
        when(reservationReader.getActiveReservationsByAssetId(1L)).thenReturn(existing);
        return reservationService.isReservationTimeAvailable(null, 1L, Instant.parse(start), Instant.parse(end));
    }

    @DisplayName("완전 비겹침 - 왼쪽")
    @Test
    void left_non_overlap() {
        List<Reservation> existing = List.of(res("2025-01-03T09:00:00Z", "2025-01-03T10:00:00Z", 1L));

        boolean ok = check("2025-01-03T07:00:00Z", "2025-01-03T08:00:00Z", existing);

        assertThat(ok).isTrue();
    }

    @DisplayName("완전 비겹침 - 오른쪽")
    @Test
    void right_non_overlap() {
        List<Reservation> existing = List.of(res("2025-01-03T09:00:00Z", "2025-01-03T10:00:00Z", 1L));

        boolean ok = check("2025-01-03T11:00:00Z", "2025-01-03T12:00:00Z", existing);

        assertThat(ok).isTrue();
    }

    @DisplayName("시각이 겹치는 경우")
    @Test
    void touching_is_allowed() {
        List<Reservation> existing = List.of(res("2025-01-03T09:00:00Z", "2025-01-03T10:00:00Z", 1L));

        boolean ok = check("2025-01-03T10:00:00Z", "2025-01-03T11:00:00Z", existing);

        assertThat(ok).isTrue();
    }

    @DisplayName("부분 겹침 (왼쪽)")
    @Test
    void partial_overlap_left() {
        List<Reservation> existing = List.of(res("2025-01-03T09:00:00Z", "2025-01-03T10:00:00Z", 1L));

        boolean ok = check("2025-01-03T08:00:00Z", "2025-01-03T09:30:00Z", existing);

        assertThat(ok).isFalse();
    }

    @DisplayName("기존 전체를 감싸는 경우")
    @Test
    void new_includes_existing() {
        List<Reservation> existing = List.of(res("2025-01-03T09:00:00Z", "2025-01-03T10:00:00Z", 1L));

        boolean ok = check("2025-01-03T08:00:00Z", "2025-01-03T12:00:00Z", existing);

        assertThat(ok).isFalse();
    }

    @DisplayName("기존 안쪽에 완전히 포함되는 경우")
    @Test
    void new_inside_existing() {
        List<Reservation> existing = List.of(res("2025-01-03T09:00:00Z", "2025-01-03T10:00:00Z", 1L));

        boolean ok = check("2025-01-03T09:30:00Z", "2025-01-03T09:45:00Z", existing);

        assertThat(ok).isFalse();
    }
}
