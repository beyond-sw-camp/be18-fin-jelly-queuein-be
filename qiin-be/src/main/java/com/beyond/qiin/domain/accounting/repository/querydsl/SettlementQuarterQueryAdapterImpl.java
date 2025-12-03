package com.beyond.qiin.domain.accounting.repository.querydsl;

import static com.beyond.qiin.domain.accounting.entity.QSettlement.settlement;
import static com.beyond.qiin.domain.accounting.entity.QUsageHistory.usageHistory;
import static com.beyond.qiin.domain.inventory.entity.QAsset.asset;

import com.beyond.qiin.domain.accounting.dto.settlement.response.raw.SettlementQuarterRowDto;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SettlementQuarterQueryAdapterImpl implements SettlementQuarterQueryAdapter {

    private final JPAQueryFactory queryFactory;

    /**
     * 분기 정산 - 월별 raw → 자원별로 분기 합산하여 1줄로 합치기
     */
    @Override
    public List<SettlementQuarterRowDto> getQuarterRows(int year, Integer quarter, String assetName) {

        int startMonth = (quarter != null) ? (quarter - 1) * 3 + 1 : 1;
        int endMonth = (quarter != null) ? startMonth + 2 : 12;

        // 월별 raw rows 조회
        List<SettlementQuarterRowDto> monthlyRows = fetchRawRows(year, startMonth, endMonth, assetName).stream()
                .map(t -> toRawDto(t, year, quarter))
                .toList();

        // 자원 + 분기 기준으로 그룹핑
        Map<String, List<SettlementQuarterRowDto>> grouped =
                monthlyRows.stream().collect(Collectors.groupingBy(r -> r.getAssetId() + "-" + r.getQuarter()));

        List<SettlementQuarterRowDto> mergedRows = new ArrayList<>();

        for (List<SettlementQuarterRowDto> rows : grouped.values()) {

            SettlementQuarterRowDto merged = SettlementQuarterRowDto.builder()
                    .assetId(rows.get(0).getAssetId())
                    .assetName(rows.get(0).getAssetName())
                    .year(rows.get(0).getYear())
                    .quarter(rows.get(0).getQuarter())
                    .reservedHours(rows.stream()
                            .mapToInt(SettlementQuarterRowDto::getReservedHours)
                            .sum())
                    .actualHours(rows.stream()
                            .mapToInt(SettlementQuarterRowDto::getActualHours)
                            .sum())
                    .totalUsageCost(rows.stream()
                            .map(SettlementQuarterRowDto::getTotalUsageCost)
                            .reduce(BigDecimal.ZERO, BigDecimal::add))
                    .actualUsageCost(rows.stream()
                            .map(SettlementQuarterRowDto::getActualUsageCost)
                            .reduce(BigDecimal.ZERO, BigDecimal::add))
                    .usageGapCost(rows.stream()
                            .map(SettlementQuarterRowDto::getUsageGapCost)
                            .reduce(BigDecimal.ZERO, BigDecimal::add))
                    .utilizationRate(null)
                    .performRate(null)
                    .utilizationGrade(null)
                    .performGrade(null)
                    .build();

            mergedRows.add(merged);
        }

        // 최종 정렬: 분기 → 자원명
        mergedRows.sort(Comparator.comparing(SettlementQuarterRowDto::getQuarter)
                .thenComparing(SettlementQuarterRowDto::getAssetName));

        return mergedRows;
    }

    /* ============================================================
      QueryDSL Raw 조회
    ============================================================ */
    private List<Tuple> fetchRawRows(int year, int startMonth, int endMonth, String assetName) {
        return queryFactory
                .select(
                        asset.id,
                        asset.name,
                        usageHistory.startAt.year(),
                        usageHistory.startAt.month(),
                        usageHistory.usageTime.sumLong(),
                        usageHistory.actualUsageTime.sumLong(),
                        settlement.totalUsageCost.sumBigDecimal(),
                        settlement.actualUsageCost.sumBigDecimal(),
                        settlement.usageGapCost.sumBigDecimal())
                .from(usageHistory)
                .join(usageHistory.asset, asset)
                .leftJoin(settlement)
                .on(settlement.usageHistory.eq(usageHistory))
                .where(
                        usageHistory.startAt.year().eq(year),
                        usageHistory.startAt.month().between(startMonth, endMonth),
                        assetName == null ? null : asset.name.containsIgnoreCase(assetName))
                .groupBy(asset.id, asset.name, usageHistory.startAt.year(), usageHistory.startAt.month())
                .fetch();
    }

    /* ============================================================
      Tuple → Raw DTO (월 단위)
    ============================================================ */
    private SettlementQuarterRowDto toRawDto(Tuple t, int year, Integer quarter) {
        Long reserved = t.get(usageHistory.usageTime.sumLong());
        Long actual = t.get(usageHistory.actualUsageTime.sumLong());
        Integer month = t.get(usageHistory.startAt.month());

        if (month == null) month = 1; // null 방지
        Integer resolvedQuarter = (quarter != null) ? quarter : quarterOfMonth(month);

        return SettlementQuarterRowDto.builder()
                .assetId(t.get(asset.id))
                .assetName(t.get(asset.name))
                .year(year)
                .quarter(resolvedQuarter)
                .reservedHours(toInt(reserved))
                .actualHours(toInt(actual))
                .utilizationRate(null)
                .performRate(null)
                .totalUsageCost(bd(t.get(settlement.totalUsageCost.sumBigDecimal())))
                .actualUsageCost(bd(t.get(settlement.actualUsageCost.sumBigDecimal())))
                .usageGapCost(bd(t.get(settlement.usageGapCost.sumBigDecimal())))
                .utilizationGrade(null)
                .performGrade(null)
                .build();
    }

    /* ============================================================
      유틸
    ============================================================ */
    private Integer toInt(Long v) {
        return v == null ? 0 : v.intValue();
    }

    private BigDecimal bd(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private int quarterOfMonth(int month) {
        if (month <= 3) return 1;
        if (month <= 6) return 2;
        if (month <= 9) return 3;
        return 4;
    }
}
