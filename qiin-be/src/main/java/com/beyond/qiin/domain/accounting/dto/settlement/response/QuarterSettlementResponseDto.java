package com.beyond.qiin.domain.accounting.dto.settlement.response;

import com.beyond.qiin.domain.accounting.dto.settlement.response.raw.QuarterSettlementRowDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class QuarterSettlementResponseDto {

    private int year; // 조회 기준 연도
    private Integer quarter; // 조회 기준 분기 (assetName 없을 때만 의미 있음)
    private List<QuarterSettlementRowDto> rows;
}
