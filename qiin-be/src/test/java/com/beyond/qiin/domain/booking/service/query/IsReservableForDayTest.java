package com.beyond.qiin.domain.booking.service.query;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.beyond.qiin.domain.booking.entity.Reservation;
import com.beyond.qiin.domain.booking.support.ReservationReader;
import com.beyond.qiin.domain.booking.support.ReservationWriter;
import com.beyond.qiin.domain.iam.support.user.UserReader;
import com.beyond.qiin.domain.inventory.service.command.AssetCommandService;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class IsReservableForDayTest {
    @InjectMocks
    private ReservationQueryServiceImpl reservationQueryService;

    @Mock
    private UserReader userReader;

    @Mock
    private ReservationReader reservationReader;

    @Mock
    private ReservationWriter reservationWriter;

    @Mock
    private AssetCommandService assetCommandService;

    @Test
    void noReservations_returnsTrue() {
        LocalDate date = LocalDate.of(2025, 12, 4);
        boolean result = reservationQueryService.isReservableForDay(date, List.of());
        assertTrue(result);
    }

    @Test
    void fullDayCovered_returnsFalse() {
        LocalDate date = LocalDate.of(2025, 12, 4);
        ZoneId zone = ZoneId.of("Asia/Seoul");

        Instant start = date.atStartOfDay(zone).toInstant();
        Instant end = date.plusDays(1).atStartOfDay(zone).toInstant();

        Reservation r = Reservation.builder().startAt(start).endAt(end).build();
        boolean result = reservationQueryService.isReservableForDay(date, List.of(r));
        assertFalse(result);
    }

    @Test
    void partialDayCovered_returnsTrue() {
        LocalDate date = LocalDate.of(2025, 12, 4);
        ZoneId zone = ZoneId.of("Asia/Seoul");

        Instant start = date.atTime(9, 0).atZone(zone).toInstant();
        Instant end = date.atTime(17, 0).atZone(zone).toInstant();

        Reservation r = Reservation.builder().startAt(start).endAt(end).build();
        boolean result = reservationQueryService.isReservableForDay(date, List.of(r));
        assertTrue(result);
    }

    @Test
    void multipleReservationsWithGap_returnsTrue() {
        LocalDate date = LocalDate.of(2025, 12, 4);
        ZoneId zone = ZoneId.of("Asia/Seoul");

        Reservation r1 = Reservation.builder()
                .startAt(date.atTime(0, 0).atZone(zone).toInstant())
                .endAt(date.atTime(8, 0).atZone(zone).toInstant())
                .build();

        Reservation r2 = Reservation.builder()
                .startAt(date.atTime(10, 0).atZone(zone).toInstant())
                .endAt(date.atTime(20, 0).atZone(zone).toInstant())
                .build();

        boolean result = reservationQueryService.isReservableForDay(date, List.of(r1, r2));
        assertTrue(result); // 8~10시 gap 존재
    }

    @Test
    void reservationsCoveringBeyondDayBoundaries_returnsFalse() {
        LocalDate date = LocalDate.of(2025, 12, 4);
        ZoneId zone = ZoneId.of("Asia/Seoul");

        Instant start = date.minusDays(1).atTime(22, 0).atZone(zone).toInstant();
        Instant end = date.plusDays(1).atTime(2, 0).atZone(zone).toInstant();

        Reservation r = Reservation.builder().startAt(start).endAt(end).build();
        boolean result = reservationQueryService.isReservableForDay(date, List.of(r));
        assertFalse(result);
    }
}
