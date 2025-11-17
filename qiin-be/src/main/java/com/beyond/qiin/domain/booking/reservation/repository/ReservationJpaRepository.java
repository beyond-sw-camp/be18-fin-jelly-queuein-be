package com.beyond.qiin.domain.booking.reservation.repository;

import com.beyond.qiin.domain.booking.reservation.entity.Reservation;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import org.antlr.v4.runtime.atn.SemanticContext.AND;
import org.antlr.v4.runtime.atn.SemanticContext.OR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {


  List<Reservation> findByAssetId(Long assetId);

  @Query("""
    SELECT r 
    FROM Reservation r
    WHERE r.status == 0
      AND r.startAt <= :endOfDay
      AND r.endAt >= :startOfDay
""")
  List<Reservation> findAllWithStatusPendingAndDate(Instant startOfDay, Instant endOfDay);


  @Query("""
    SELECT r
    FROM Reservation r
    WHERE r.startAt <= :startOfMonth
      AND r.endAt >= :endOfMonth
""")
  List<Reservation> findByUserIdAndYearMonth(Long userId, Instant startOfMonth, Instant endOfMonth);

  @Query("""
    SELECT r 
    FROM Reservation r
    WHERE r.asset.id = :assetId
      AND r.startAt <= :endOfDay
      AND r.endAt >= :startOfDay
"""
  )
  List<Reservation> findAllByAssetIdAndDate(Long assetId, Instant startOfDay, Instant endOfDay);

  @Query("""
    SELECT r
    FROM Reservation r
    WHERE r.applicant.id = :userId
      AND r.startAt <= :endOfDay
      AND r.endAt >= :startOfDay
""")
  List<Reservation> findByUserIdAndDate(Long userId, Instant startOfDay, Instant endOfDay);
}
