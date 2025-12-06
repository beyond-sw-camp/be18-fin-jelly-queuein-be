package com.beyond.qiin.domain.booking.service.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.beyond.qiin.domain.booking.dto.reservation.request.ConfirmReservationRequestDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservationResponseDto;
import com.beyond.qiin.domain.booking.entity.Reservation;
import com.beyond.qiin.domain.booking.enums.ReservationStatus;
import com.beyond.qiin.domain.booking.event.ReservationEventPublisher;
import com.beyond.qiin.domain.booking.repository.AttendantJpaRepository;
import com.beyond.qiin.domain.booking.support.AttendantWriter;
import com.beyond.qiin.domain.booking.support.ReservationReader;
import com.beyond.qiin.domain.booking.support.ReservationValidator;
import com.beyond.qiin.domain.booking.support.ReservationWriter;
import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.support.user.UserReader;
import com.beyond.qiin.domain.inventory.entity.Asset;
import com.beyond.qiin.domain.inventory.service.command.AssetCommandService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ApproveReservationTest {
    @Spy
    @InjectMocks
    private ReservationCommandServiceImpl reservationCommandService;

    @Mock
    private ReservationValidator reservationValidator;

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
        Long userId = 1L;
        Long reservationId = 10L;
        ConfirmReservationRequestDto dto =
                ConfirmReservationRequestDto.builder().reason("승인 이유").build();

        User user = User.builder().userName("tester").build();
        User applicant = User.builder().userName("신청자").build();
        Asset asset = Asset.builder().name("회의실").build();
        Reservation reservation = Mockito.spy(
                Reservation.builder().asset(asset).applicant(applicant).build());

        Mockito.when(userReader.findById(userId)).thenReturn(user);
        Mockito.when(reservationReader.getReservationById(reservationId)).thenReturn(reservation);

        ReservationResponseDto response = reservationCommandService.approveReservation(userId, reservationId, dto);

        Mockito.verify(reservation).approve(user, "승인 이유");
        Mockito.verify(reservationWriter).save(reservation);
        Mockito.verify(reservationEventPublisher).publishUpdated(reservation);

        assertNotNull(response); // DTO가 반환되는지
        assertEquals(ReservationStatus.APPROVED, reservation.getStatus());
    }
}
