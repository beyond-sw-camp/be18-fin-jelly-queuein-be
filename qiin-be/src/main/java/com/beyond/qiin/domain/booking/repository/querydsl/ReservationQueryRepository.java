package com.beyond.qiin.domain.booking.repository.querydsl;

import com.beyond.qiin.domain.booking.entity.Reservation;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface ReservationQueryRepository {

    Map<Long, List<Reservation>> findByAssetIdsAndTimeRange(List<Long> assetIds, Instant dayStart, Instant dayEnd);
}
