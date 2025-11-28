package com.beyond.qiin.domain.accounting.dto.settlement.response.raw;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class QuarterSettlementExcelRowDto {

    private int year;
    private int quarter;

    private Long assetId;
    private String assetName;

    private Integer reservedHours;
    private Integer actualHours;

    private Double utilizationRate;
    private Double performRate;

    private BigDecimal totalUsageCost;
    private BigDecimal actualUsageCost;
    private BigDecimal usageGapCost;

    private String utilizationGrade;
    private String performGrade;
}
