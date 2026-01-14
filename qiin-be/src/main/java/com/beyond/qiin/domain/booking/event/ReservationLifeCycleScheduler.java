package com.beyond.qiin.domain.booking.event;

import com.beyond.qiin.domain.booking.entity.Reservation;
import com.beyond.qiin.domain.booking.repository.ReservationJpaRepository;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ReservationLifeCycleScheduler {

    private final ReservationJpaRepository reservationJpaRepository;

    @Scheduled(fixedDelay = 60000) // 1분
    @Transactional
    public void completeExpiredReservations() {

        Instant now = Instant.now();

        List<Reservation> expired = reservationJpaRepository.findUsingOrStartedButExpired(now);

        for (Reservation r : expired) {
            r.end();
        }
    }
}
