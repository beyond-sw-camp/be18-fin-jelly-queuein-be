package com.beyond.qiin.domain.booking.event;

import java.util.List;

public record ReservationChangedEvent(Long applicantUserId, List<Long> attendantUserIds) {}
