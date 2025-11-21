package com.beyond.qiin.domain.booking.reservation.attendant.repository;

import com.beyond.qiin.domain.booking.reservation.attendant.entity.Attendant;
import com.beyond.qiin.domain.booking.reservation.entity.Reservation;
import org.springframework.data.repository.CrudRepository;

public interface ReservationRedisRepository extends CrudRepository<Attendant, Long> {

}
