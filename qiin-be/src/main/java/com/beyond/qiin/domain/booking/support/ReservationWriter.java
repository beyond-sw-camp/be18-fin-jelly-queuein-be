package com.beyond.qiin.domain.booking.support;

import com.beyond.qiin.domain.booking.entity.Reservation;
import com.beyond.qiin.domain.booking.repository.ReservationJpaRepository;
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
