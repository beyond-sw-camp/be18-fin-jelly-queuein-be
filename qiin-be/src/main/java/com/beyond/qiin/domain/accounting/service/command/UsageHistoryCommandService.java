package com.beyond.qiin.domain.accounting.service.command;

import com.beyond.qiin.domain.accounting.entity.UsageHistory;
import com.beyond.qiin.domain.booking.entity.Reservation;
import com.beyond.qiin.domain.inventory.entity.Asset;

public interface UsageHistoryCommandService {
    UsageHistory createUsageHistory(Asset asset, Reservation reservation);
}
