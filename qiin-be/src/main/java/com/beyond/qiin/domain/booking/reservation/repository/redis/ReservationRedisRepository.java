package com.beyond.qiin.domain.booking.reservation.repository.redis;

import com.beyond.qiin.domain.booking.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface ReservationRedisRepository extends CrudRepository<Reservation,Long> {

}
