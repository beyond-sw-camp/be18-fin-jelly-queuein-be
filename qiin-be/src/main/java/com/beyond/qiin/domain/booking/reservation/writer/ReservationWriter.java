package com.beyond.qiin.domain.booking.reservation.writer;

import com.beyond.qiin.domain.booking.reservation.entity.Reservation;
import com.beyond.qiin.domain.booking.reservation.repository.ReservationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationWriter {
    private final ReservationJpaRepository reservationJpaRepository;

    public void save(Reservation reservation) {
        reservationJpaRepository.save(reservation);
    }
}
