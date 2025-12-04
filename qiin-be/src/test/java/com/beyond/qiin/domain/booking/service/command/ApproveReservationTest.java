//package com.beyond.qiin.domain.booking.service.command;
//
//import com.beyond.qiin.domain.booking.dto.reservation.request.ConfirmReservationRequestDto;
//import com.beyond.qiin.domain.booking.dto.reservation.response.ReservationResponseDto;
//import com.beyond.qiin.domain.booking.entity.Reservation;
//import com.beyond.qiin.domain.booking.enums.ReservationStatus;
//import com.beyond.qiin.domain.iam.entity.User;
//import com.beyond.qiin.domain.inventory.entity.Asset;
//import java.util.List;
//import org.junit.jupiter.api.Test;
//
//public class ApproveReservationTest {
//
//  @Test
//  void approveReservation_success() {
//    // given
//    Long userId = 1L;
//    Long reservationId = 10L;
//
//    ConfirmReservationRequestDto requestDto =
//        ConfirmReservationRequestDto.builder().reason("승인 사유").build();
//
//    User respondent = User.builder()
//        .userName("승인자")
//        .build();
//
//    Asset asset = Asset.builder()
//        .name("회의실 A")
//        .build();
//
//    Reservation reservation = Reservation.builder()
//        .asset(asset)
//        .startAt(startAt)
//        .endAt(endAt)
//        .status(ReservationStatus.WAITING)
//        .build();
//
//    reservation.setId(reservationId);
//
//    when(reservationReader.getReservationsByAssetAndDate(any(), any()))
//        .thenReturn(List.of());
//
//    when(userReader.findById(userId)).thenReturn(respondent);
//    when(reservationReader.getReservationById(reservationId)).thenReturn(reservation);
//
//    doNothing().when(reservationWriter).save(any());
//    doNothing().when(reservationEventPublisher).publishUpdated(any());
//
//    // when
//    ReservationResponseDto result =
//        reservationCommandService.approveReservation(
//            userId, reservationId, requestDto);
//
//    assertNotNull(result);
//    assertEquals(ReservationStatus.APPROVED, result.getStatus());
//    assertEquals("회의실 A", result.getAssetName());
//    assertEquals("승인자", result.getRespondentName());
//
//    verify(reservationWriter).save(reservation);
//    verify(reservationEventPublisher).publishUpdated(reservation);
//  }
//
//}
//
