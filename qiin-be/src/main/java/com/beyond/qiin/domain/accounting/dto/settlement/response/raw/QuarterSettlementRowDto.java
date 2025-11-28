package com.beyond.qiin.domain.accounting.dto.settlement.response.raw;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class QuarterSettlementRowDto {

    private Long assetId;
    private String assetName;

    private int year;
    private int quarter;

    private Integer reservedHours; // 예약 시간
    private Integer actualHours; // 실제 사용 시간

    private Double utilizationRate; // 활용률 (가용 대비 예약)
    private Double performRate; // 예약 대비 사용률

    private BigDecimal totalUsageCost; // 예약 금액
    private BigDecimal actualUsageCost; // 실제 사용 금액
    private BigDecimal usageGapCost; // 절감/낭비 금액

    private String utilizationGrade; // 가동률 등급 (A/B/C)
    private String performGrade; // 활용률 등급 (A/B/C)
}
