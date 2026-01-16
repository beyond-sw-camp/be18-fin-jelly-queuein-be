package com.beyond.qiin.domain.booking.event;

import com.beyond.qiin.domain.booking.enums.ReservationStatus;

import java.util.List;

public record ReservationChangedEvent(
        Long applicantUserId,

        List<Long> attendantUserIds
) {
}