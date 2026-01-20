package com.beyond.qiin.domain.booking.service.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.beyond.qiin.domain.accounting.service.command.UsageHistoryCommandService;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservationResponseDto;
import com.beyond.qiin.domain.booking.entity.Reservation;
import com.beyond.qiin.domain.booking.enums.ReservationStatus;
import com.beyond.qiin.domain.booking.event.ReservationExternalEventPublisher;
import com.beyond.qiin.domain.booking.event.ReservationInternalEventPublisher;
import com.beyond.qiin.domain.booking.exception.ReservationErrorCode;
import com.beyond.qiin.domain.booking.exception.ReservationException;
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
public class StartReservationTest {

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

    @Test
    void startUsingReservation_success() {
        // given
        Long userId = 1L;
        Long reservationId = 10L;
        Instant now = Instant.now();
        User user = User.builder().userName("A").build();
        Asset asset = Asset.builder().name("회의실 A").build();
        Reservation reservation = Reservation.builder()
                .asset(asset)
                .applicant(user)
                .startAt(now.minusSeconds(60 * 10))
                .endAt(now.plusSeconds(60 * 50))
                .status(ReservationStatus.APPROVED.getCode()) // 시작 가능 상태
                .build();

        when(userReader.findById(userId)).thenReturn(user);
        when(reservationReader.getReservationById(reservationId)).thenReturn(reservation);
        when(assetCommandService.isAvailable(reservation.getAsset().getId())).thenReturn(true);
        doNothing().when(reservationWriter).save(reservation);

        // when
        ReservationResponseDto responseDto = reservationCommandService.startUsingReservation(userId, reservationId);

        // then
        assertNotNull(responseDto);
        assertEquals(ReservationStatus.USING.name(), responseDto.getStatus()); // status 변경 확인
        assertEquals("회의실 A", responseDto.getAssetName());
        assertNotNull(responseDto.getActualStartAt()); // 실제 시작 시간 기록 확인

        verify(userReader).findById(userId);
        verify(reservationReader).getReservationById(reservationId);
        verify(assetCommandService).isAvailable(asset.getId());
        verify(reservationWriter).save(reservation);
    }

    @Test
    void startUsingReservation_beforeStartTime_shouldFail() {
        // given
        Long userId = 1L;
        Long reservationId = 10L;

        User user = User.builder().userName("A").build();
        Asset asset = Asset.builder().name("회의실 A").build();

        Instant startAt = Instant.now().plusSeconds(10 * 60);
        Instant endAt = startAt.plusSeconds(60 * 60);

        Reservation reservation = Reservation.builder()
                .asset(asset)
                .applicant(user)
                .startAt(startAt)
                .endAt(endAt)
                .status(ReservationStatus.APPROVED.getCode())
                .build();

        when(userReader.findById(userId)).thenReturn(user);
        when(reservationReader.getReservationById(reservationId)).thenReturn(reservation);
        when(assetCommandService.isAvailable(asset.getId())).thenReturn(true);

        // when & then - 시작 시간이 10분 뒤이므로 더 이른 시간에 대한 검증
        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> reservationCommandService.startUsingReservation(userId, reservationId));

        assertEquals(ReservationErrorCode.RESERVATION_TIME_NOT_YET, exception.getErrorCode());
    }
}
