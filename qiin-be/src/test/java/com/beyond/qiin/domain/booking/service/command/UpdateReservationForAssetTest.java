package com.beyond.qiin.domain.booking.service.command;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.beyond.qiin.domain.booking.entity.Reservation;
import com.beyond.qiin.domain.booking.support.ReservationWriter;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UpdateReservationForAssetTest {

  @InjectMocks
  private ReservationCommandServiceImpl reservationCommandService;

  @Mock
  private ReservationWriter reservationWriter;

  @Mock
  private Reservation reservation1;

  @Mock
  private Reservation reservation2;

  @Test
  void updateReservationsForAsset_unavailable_shouldCancelReservations() {
    Long assetId = 100L;

    when(reservationWriter.findFutureUsableReservations(assetId))
        .thenReturn(List.of(reservation1, reservation2));

    // when
    reservationCommandService.updateReservationsForAsset(assetId, 1); // UNAVAILABLE

    // then
    verify(reservation1).markUnavailable("자원 사용 불가 상태에 따른 자동 취소");
    verify(reservation2).markUnavailable("자원 사용 불가 상태에 따른 자동 취소");
  }

}
