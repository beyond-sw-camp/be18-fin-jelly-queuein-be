package com.beyond.qiin.domain.booking.repository.querydsl;

import com.beyond.qiin.domain.booking.entity.QReservation;
import com.beyond.qiin.domain.booking.entity.Reservation;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReservationQueryRepositoryImpl implements ReservationQueryRepository {

    private final JPAQueryFactory queryFactory;
    private static final QReservation reservation = QReservation.reservation;

    public Map<Long, List<Reservation>> findByAssetIdsAndTimeRange(
            List<Long> assetIds, Instant dayStart, Instant dayEnd) {

        List<Reservation> reservations = queryFactory
                .selectFrom(reservation)
                .where(
                        reservation.asset.id.in(assetIds),
                        reservation.startAt.lt(dayEnd),
                        reservation.endAt.gt(dayStart))
                .fetch();

        return reservations.stream()
                .collect(Collectors.groupingBy(r -> r.getAsset().getId()));
    }
}
