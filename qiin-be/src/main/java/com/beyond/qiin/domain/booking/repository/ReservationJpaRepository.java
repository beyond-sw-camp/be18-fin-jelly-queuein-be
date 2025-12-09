package com.beyond.qiin.domain.booking.repository;

import com.beyond.qiin.domain.booking.entity.Reservation;
import com.beyond.qiin.domain.booking.enums.ReservationStatus;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByAssetId(Long assetId);

    @Query(
            """
    SELECT r
    FROM Reservation r
    WHERE r.status = 0
      AND r.startAt <= :endOfDay
      AND r.endAt >= :startOfDay
""")
    Page<Reservation> findAllWithStatusPendingAndDate(Instant startOfDay, Instant endOfDay, Pageable pageable);

    @Query(
            """
    SELECT r
    FROM Reservation r
    WHERE r.applicant.id = :userId
      AND r.startAt <= :to
      AND r.endAt >= :from
      AND r.status IN (1, 2, 5)
""")
    List<Reservation> findByUserIdAndYearMonth(Long userId, Instant from, Instant to);

    @Query(
            """
    select r from Reservation r
    where r.applicant.id = :userId
      and r.startAt between :start and :end
      and r.status in (1, 2, 5)
    order by r.startAt asc
""")
    List<Reservation> findByUserIdAndWeek(Long userId, Instant start, Instant end);

    @Query(
            """
    SELECT r
    FROM Reservation r
    WHERE r.asset.id = :assetId
      AND r.startAt <= :endOfDay
      AND r.endAt >= :startOfDay

      AND r.status IN (1, 2, 5)
""")
    List<Reservation> findAllByAssetIdAndDate(Long assetId, Instant startOfDay, Instant endOfDay);

    @Query(
            """
    SELECT r
    FROM Reservation r
    WHERE r.applicant.id = :userId
      AND r.startAt <= :endOfDay
      AND r.endAt >= :startOfDay
""")
    List<Reservation> findByUserIdAndDate(Long userId, Instant startOfDay, Instant endOfDay);

    @Query(
            """
    SELECT r
    FROM Reservation r
    WHERE r.asset.id = :assetId
      AND r.startAt > CURRENT_TIMESTAMP
      AND r.status IN (0, 1, 2)
""")
    List<Reservation> findFutureUsableReservationsByAsset(Long assetId);

    @Query("""
    SELECT r FROM Reservation r
    WHERE r.asset.id = :assetId
    AND r.status IN (1, 2, 5)

""")
    List<Reservation> findActiveReservationsByAssetId(Long assetId);

    @Query("""
        SELECT r 
        FROM Reservation r 
        WHERE r.startAt BETWEEN :start AND :end
          AND r.status = 1
    """)
    List<Reservation> findReservationsStartingBetween(
        Instant start,
        Instant end
    );

}
