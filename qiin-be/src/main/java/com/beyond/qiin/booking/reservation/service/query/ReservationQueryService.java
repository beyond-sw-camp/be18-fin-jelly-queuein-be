package com.beyond.qiin.booking.reservation.service.query;

import com.beyond.qiin.booking.reservation.entity.Reservation;
import java.util.List;

public interface ReservationQueryService {
  Reservation getReservationById(Long id);
  List<Reservation> getReservationsByAssetId(Long assetId);
  List<Reservation> getReservationsByUserId(Long userId);
  Reservation getReservationById(Long id);
  List<Reservation> getReservationsByAssetId(Long assetId);
  List<Reservation> getReservationApplies();
}
