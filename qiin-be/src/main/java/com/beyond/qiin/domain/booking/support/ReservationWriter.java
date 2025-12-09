package com.beyond.qiin.domain.booking.support;

import com.beyond.qiin.domain.booking.entity.Reservation;
import com.beyond.qiin.domain.booking.event.ReservationEventPublisher;
import com.beyond.qiin.domain.booking.repository.ReservationJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ReservationWriter {
    private final ReservationJpaRepository reservationJpaRepository;
    private final ReservationReader reservationReader;
    private final ReservationEventPublisher reservationEventPublisher;

    public void save(Reservation reservation) {
        reservationJpaRepository.save(reservation);
    }

    // 자원 상태 변경 시 예약 상태 변경
    @Transactional
    public void updateReservationsForAsset(final Long assetId, final Integer assetStatus) {
        // 1 = UNAVAILABLE, 2 = MAINTENANCE
        if (assetStatus != 1 && assetStatus != 2) return;
        if (assetStatus == null) return;
        // pending, approved, using 대상(0, 1, 2)
        List<Reservation> reservations = findFutureUsableReservations(assetId);

        for (Reservation reservation : reservations) {
            reservation.markUnavailable("자원 사용 불가 상태에 따른 자동 취소");

            List<Long> attendantUserIds = reservation.getAttendants().stream()
                    .map(a -> a.getUser().getId())
                    .toList(); // 각 attendantUserId 에 대해 넣지 못하는 문제 userId를 인자로 지정하게 해줘야하나

            reservationEventPublisher.publishEventCreated(reservation, attendantUserIds);
        }
    }

    @Transactional
    public void hardDelete(Long reservationId) {
        Reservation reservation = reservationReader.getReservationById(reservationId);
        reservationJpaRepository.delete(reservation);
    }

    // TODO : reader로 옮겨야함
    @Transactional(readOnly = true)
    public List<Reservation> findFutureUsableReservations(Long assetId) {
        return reservationJpaRepository.findFutureUsableReservationsByAsset(assetId);
    }
}
