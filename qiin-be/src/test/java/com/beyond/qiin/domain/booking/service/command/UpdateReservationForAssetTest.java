package com.beyond.qiin.domain.booking.service.command;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.beyond.qiin.domain.booking.entity.Reservation;
import com.beyond.qiin.domain.booking.event.ReservationExternalEventPublisher;
import com.beyond.qiin.domain.booking.repository.ReservationJpaRepository;
import com.beyond.qiin.domain.booking.support.ReservationReader;
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
    private ReservationWriter reservationWriter;

    @Mock
    private ReservationExternalEventPublisher reservationExternalEventPublisher;

    @Mock
    private ReservationJpaRepository reservationJpaRepository;

    @Mock
    private ReservationReader reservationReader;

    @Mock
    private Reservation reservation1;

    @Mock
    private Reservation reservation2;

    @Test
    void updateReservationsForAsset_unavailable_shouldCancelReservations() {
        Long assetId = 100L;
        int assetStatus = 1; // UNAVAILABLE

        // Writer 내부 호출 (repository) mock
        when(reservationJpaRepository.findFutureUsableReservationsByAsset(assetId))
                .thenReturn(List.of(reservation1, reservation2));

        // when: 실제 로직 실행
        reservationWriter.updateReservationsForAsset(assetId, assetStatus);

        // then: markUnavailable 호출되었는지 검증
        verify(reservation1).markUnavailable("자원 사용 불가 상태에 따른 자동 취소");
        verify(reservation2).markUnavailable("자원 사용 불가 상태에 따른 자동 취소");
    }
}
