package com.beyond.qiin.domain.booking.service.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.beyond.qiin.domain.booking.entity.Reservation;
import com.beyond.qiin.domain.booking.enums.ReservationStatus;
import com.beyond.qiin.domain.booking.exception.ReservationErrorCode;
import com.beyond.qiin.domain.booking.exception.ReservationException;
import com.beyond.qiin.domain.booking.support.ReservationReader;
import com.beyond.qiin.domain.booking.support.ReservationWriter;
import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.support.user.UserReader;
import com.beyond.qiin.domain.inventory.entity.Asset;
import com.beyond.qiin.domain.inventory.service.command.AssetCommandService;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EndReservationTest {
  @InjectMocks
  private ReservationCommandServiceImpl reservationCommandService;

  @Mock
  private UserReader userReader;

  @Mock
  private ReservationReader reservationReader;

  @Mock
  private ReservationWriter reservationWriter;

  @Mock
  private AssetCommandService assetCommandService;

  //사용 시작하지 않은 예약을 사용 종료 불가
  @Test
  void endUsingReservation_fail() {
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

    // when & then
    ReservationException exception = assertThrows(ReservationException.class, () ->
        reservationCommandService.endUsingReservation(userId, reservationId)
    );

    assertEquals(ReservationErrorCode.RESERVATION_STATUS_CHANGE_NOT_ALLOWED, exception.getErrorCode());

  }
  
}
