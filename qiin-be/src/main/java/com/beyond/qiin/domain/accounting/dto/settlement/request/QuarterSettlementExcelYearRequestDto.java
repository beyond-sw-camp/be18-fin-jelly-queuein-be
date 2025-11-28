package com.beyond.qiin.domain.accounting.dto.settlement.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuarterSettlementExcelYearRequestDto {

    @NotNull
    private Integer year;

    private Integer quarter;
}
