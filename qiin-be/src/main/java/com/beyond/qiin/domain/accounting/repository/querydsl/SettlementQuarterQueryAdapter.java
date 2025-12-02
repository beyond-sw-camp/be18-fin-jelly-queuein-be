package com.beyond.qiin.domain.accounting.repository.querydsl;

import com.beyond.qiin.domain.accounting.dto.settlement.response.raw.SettlementQuarterRowDto;
import java.util.List;

public interface SettlementQuarterQueryAdapter {

    List<SettlementQuarterRowDto> getQuarterRows(int year, Integer quarter, String assetName);
}
