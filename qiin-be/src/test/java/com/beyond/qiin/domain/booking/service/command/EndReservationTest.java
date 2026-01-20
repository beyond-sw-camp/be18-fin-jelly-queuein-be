package com.beyond.qiin.domain.booking.service.command;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.beyond.qiin.domain.accounting.service.command.UsageHistoryCommandService;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservationResponseDto;
import com.beyond.qiin.domain.booking.entity.Reservation;
import com.beyond.qiin.domain.booking.enums.ReservationStatus;
import com.beyond.qiin.domain.booking.event.ReservationExternalEventPublisher;
import com.beyond.qiin.domain.booking.event.ReservationInternalEventPublisher;
import com.beyond.qiin.domain.booking.repository.AttendantJpaRepository;
import com.beyond.qiin.domain.booking.support.AttendantWriter;
import com.beyond.qiin.domain.booking.support.ReservationReader;
import com.beyond.qiin.domain.booking.support.ReservationWriter;
import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.support.user.UserReader;
import com.beyond.qiin.domain.inventory.entity.Asset;
import com.beyond.qiin.domain.inventory.service.command.AssetCommandService;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EndReservationTest {

    private ReservationCommandServiceImpl reservationCommandService;

    @Mock
    private UserReader userReader;

    @Mock
    private ReservationReader reservationReader;

    @Mock
    private ReservationWriter reservationWriter;

    @Mock
    private AssetCommandService assetCommandService;

    @Mock
    private AttendantWriter attendantWriter;

    @Mock
    private ReservationExternalEventPublisher reservationExternalEventPublisher;

    @Mock
    private ReservationInternalEventPublisher reservationInternalEventPublisher;

    @Mock
    private AttendantJpaRepository attendantJpaRepository;

    @Mock
    private UsageHistoryCommandService usageHistoryCommandService;

    @BeforeEach
    void setUp() {
        reservationCommandService = new ReservationCommandServiceImpl(
                userReader,
                reservationReader,
                reservationWriter,
                attendantWriter,
                assetCommandService,
                reservationExternalEventPublisher,
                reservationInternalEventPublisher,
                attendantJpaRepository,
                usageHistoryCommandService);
    }

    // 사용 시작과 상관없이 예약 자원 사용 종료 가능
    @Test
    void endUsingReservation_success() {
        Long userId = 1L;
        Long reservationId = 10L;

        User user = User.builder().userName("A").build();
        Asset asset = Asset.builder().name("회의실 A").build();
        Reservation reservation = Reservation.builder()
                .asset(asset)
                .applicant(user)
                .startAt(Instant.parse("2025-12-04T10:00:00Z"))
                .endAt(Instant.parse("2025-12-04T11:00:00Z"))
                .status(ReservationStatus.APPROVED.getCode()) // USING이 아닌 상태
                .build();

        when(userReader.findById(userId)).thenReturn(user);
        when(reservationReader.getReservationById(reservationId)).thenReturn(reservation);

        // when
        ReservationResponseDto response = reservationCommandService.endUsingReservation(userId, reservationId);

        // then
        assertNotNull(response);
        assertEquals(ReservationStatus.COMPLETED.name(), response.getStatus());

        assertNotNull(response.getActualEndAt());

        verify(reservationWriter).save(reservation);
        verify(usageHistoryCommandService).createUsageHistory(asset, reservation);
    }
}
