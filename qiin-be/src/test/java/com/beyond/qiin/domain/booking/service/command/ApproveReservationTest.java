 package com.beyond.qiin.domain.booking.service.command;

 import static org.junit.jupiter.api.Assertions.assertEquals;
 import static org.junit.jupiter.api.Assertions.assertNotNull;
 import static org.mockito.ArgumentMatchers.any;
 import static org.mockito.Mockito.doNothing;
 import static org.mockito.Mockito.verify;
 import static org.mockito.Mockito.when;

 import com.beyond.qiin.domain.booking.dto.reservation.request.ConfirmReservationRequestDto;
 import com.beyond.qiin.domain.booking.dto.reservation.response.ReservationResponseDto;
 import com.beyond.qiin.domain.booking.entity.Reservation;
 import com.beyond.qiin.domain.booking.enums.ReservationStatus;
 import com.beyond.qiin.domain.booking.event.ReservationEventPublisher;
 import com.beyond.qiin.domain.booking.repository.AttendantJpaRepository;
 import com.beyond.qiin.domain.booking.support.AttendantWriter;
 import com.beyond.qiin.domain.booking.support.ReservationReader;
 import com.beyond.qiin.domain.booking.support.ReservationWriter;
 import com.beyond.qiin.domain.iam.entity.User;
 import com.beyond.qiin.domain.iam.support.user.UserReader;
 import com.beyond.qiin.domain.inventory.entity.Asset;
 import com.beyond.qiin.domain.inventory.service.command.AssetCommandService;
 import java.time.Instant;
 import java.util.List;
 import org.junit.jupiter.api.Test;
 import org.junit.jupiter.api.extension.ExtendWith;
 import org.mockito.InjectMocks;
 import org.mockito.Mock;
 import org.mockito.Spy;
 import org.mockito.junit.jupiter.MockitoExtension;

 @ExtendWith(MockitoExtension.class)
 public class ApproveReservationTest {
    @Spy
    @InjectMocks
    private ReservationCommandServiceImpl reservationCommandService;

    @Mock
    private UserReader userReader;

    @Mock
    private ReservationReader reservationReader;

    @Mock
    private ReservationWriter reservationWriter;

    @Mock
    private AttendantWriter attendantWriter;

    @Mock
    private AssetCommandService assetCommandService;

    @Mock
    private ReservationEventPublisher reservationEventPublisher;

    @Mock
    private AttendantJpaRepository attendantJpaRepository;

    @Test
    void approveReservation_success() {
        // given
        Long userId = 1L;
        Long reservationId = 10L;
        ConfirmReservationRequestDto requestDto =
                ConfirmReservationRequestDto.builder().reason("승인 사유").build();

        User respondent = User.builder().userName("A").build();
        User applicant = User.builder().userName("B").build();
        Asset asset = Asset.builder().name("회의실 A").build();
        Reservation reservation = Reservation.builder()
                .asset(asset)
                .applicant(applicant)
                .startAt(Instant.parse("2025-12-04T10:00:00Z"))
                .endAt(Instant.parse("2025-12-04T11:00:00Z"))
                .status(ReservationStatus.PENDING.getCode())
                .build();


        reservation.setApplicant(applicant);
        reservation.setRespondent(respondent);
        when(reservationReader.getReservationsByAssetAndDate(any(), any())).thenReturn(List.of());

        when(userReader.findById(userId)).thenReturn(respondent);
        when(reservationReader.getReservationById(reservationId)).thenReturn(reservation);

        doNothing().when(reservationWriter).save(any());
        doNothing().when(reservationEventPublisher).publishUpdated(any());

        // when
        ReservationResponseDto reservationResponseDto = reservationCommandService.approveReservation(userId,
 reservationId, requestDto);

        assertNotNull(reservationResponseDto);
        assertEquals("APPROVED", reservationResponseDto.getStatus());
        assertEquals("회의실 A", reservationResponseDto.getAssetName());

        verify(reservationWriter).save(reservation);
        verify(reservationEventPublisher).publishUpdated(reservation);
    }
 }
