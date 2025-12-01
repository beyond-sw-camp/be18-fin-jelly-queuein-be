package com.beyond.qiin.domain.accounting.service.command;

import com.beyond.qiin.domain.accounting.entity.UsageHistory;
import com.beyond.qiin.domain.booking.entity.Reservation;

public interface UserHistoryCommandService {

    void createUserHistories(Reservation reservation, UsageHistory usageHistory);
}
