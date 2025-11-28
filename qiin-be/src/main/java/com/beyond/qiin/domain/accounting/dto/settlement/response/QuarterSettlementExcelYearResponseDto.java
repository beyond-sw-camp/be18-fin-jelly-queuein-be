package com.beyond.qiin.domain.accounting.dto.settlement.response;

import com.beyond.qiin.domain.accounting.dto.settlement.response.raw.QuarterSettlementExcelRowDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class QuarterSettlementExcelYearResponseDto {

    private Integer year;

    // 1~4분기 각각의 리스트
    private List<QuarterSettlementExcelRowDto> q1;
    private List<QuarterSettlementExcelRowDto> q2;
    private List<QuarterSettlementExcelRowDto> q3;
    private List<QuarterSettlementExcelRowDto> q4;
}
