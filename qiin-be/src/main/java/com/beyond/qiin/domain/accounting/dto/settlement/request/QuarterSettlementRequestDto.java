package com.beyond.qiin.domain.accounting.dto.settlement.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuarterSettlementRequestDto {

    @NotNull
    private Integer year; // 조회할 연도 (필수)

    private Integer quarter; // 조회할 분기 (옵션 → 없으면 최신 분기)

    private String assetName; // 자원명 검색 (옵션)
}
