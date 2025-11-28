package com.beyond.qiin.domain.booking.support;

import com.beyond.qiin.domain.booking.entity.Reservation;
import com.beyond.qiin.domain.booking.repository.ReservationJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationWriter {
    private final ReservationJpaRepository reservationJpaRepository;
    private final ReservationReader reservationReader;

    public void save(Reservation reservation) {
        reservationJpaRepository.save(reservation);
    }

    public void hardDelete(Long reservationId) {
        Reservation reservation = reservationReader.getReservationById(reservationId);
        reservationJpaRepository.delete(reservation);
    }

    public List<Reservation> findFutureUsableReservations(Long assetId) {
        return reservationJpaRepository.findFutureUsableReservationsByAsset(assetId);
    }
}
