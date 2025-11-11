package com.beyond.qiin.booking.reservation.repository;

import com.beyond.qiin.booking.reservation.entity.Reservation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {

  List<Reservation> findByAssetId(Long assetId);

}
