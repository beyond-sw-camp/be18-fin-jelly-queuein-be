package com.beyond.qiin.domain.accounting.service.command;

import com.beyond.qiin.domain.accounting.entity.Settlement;
import com.beyond.qiin.domain.accounting.entity.UsageHistory;

public interface SettlementCommandService {
    Settlement createSettlement(UsageHistory usageHistory);
}
