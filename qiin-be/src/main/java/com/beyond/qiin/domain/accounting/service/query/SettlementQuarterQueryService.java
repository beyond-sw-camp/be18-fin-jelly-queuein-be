package com.beyond.qiin.domain.accounting.service.query;

import com.beyond.qiin.domain.accounting.dto.settlement.request.SettlementQuarterRequestDto;
import com.beyond.qiin.domain.accounting.dto.settlement.response.SettlementQuarterResponseDto;

public interface SettlementQuarterQueryService {

    /**
     * 분기별 정산 조회
     * - 특정 연도 + 분기 + 자원명 조건으로 화면 조회용 데이터 반환
     */
    SettlementQuarterResponseDto getQuarter(SettlementQuarterRequestDto req);
}
