package com.beyond.qiin.domain.booking.repository;

import com.beyond.qiin.domain.booking.entity.Reservation;
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
""")
    List<Reservation> findByUserIdAndYearMonth(Long userId, Instant from, Instant to);

    @Query(
            """
    select r from Reservation r
    where r.applicant.id = :userId
      and r.startAt between :start and :end
    order by r.startAt asc
""")
    List<Reservation> findByUserIdAndWeek(Long userId, Instant start, Instant end);

    @Query(
            """
    SELECT r
    FROM Reservation r
    WHERE r.asset.id = :assetId
      AND NOT (
        r.startAt >= :endOfDay
        OR r.endAt <= :startOfDay
      )
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
}
