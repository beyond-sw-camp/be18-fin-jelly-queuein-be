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
     * 화면 조회용 분기 정산
     */
    @Override
    public List<SettlementQuarterRowDto> getQuarterRows(int year, Integer quarter, String assetName) {

        // 전체 월 범위 계산
        int startMonth = (quarter != null) ? (quarter - 1) * 3 + 1 : 1;
        int endMonth = (quarter != null) ? startMonth + 2 : 12;

        // DB 조회
        List<SettlementQuarterRowDto> allRows = fetchRawRows(year, startMonth, endMonth, assetName).stream()
                .map(t -> toRawDto(t, year, quarter))
                .toList();

        // 분기별 그룹핑 + 최신 분기부터 정렬
        Map<Integer, List<SettlementQuarterRowDto>> grouped =
                allRows.stream().collect(Collectors.groupingBy(SettlementQuarterRowDto::getQuarter));

        List<SettlementQuarterRowDto> sortedRows = new ArrayList<>();
        for (int q = 4; q >= 1; q--) {
            List<SettlementQuarterRowDto> rows = grouped.getOrDefault(q, Collections.emptyList());
            rows.sort(Comparator.comparing(SettlementQuarterRowDto::getAssetName));
            sortedRows.addAll(rows);
        }

        return sortedRows;
    }

    /**
     * QueryDSL 공통 조회
     */
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

    /**
     * DTO 변환
     */
    private SettlementQuarterRowDto toRawDto(Tuple t, int year, Integer quarter) {
        Long reserved = t.get(usageHistory.usageTime.sumLong());
        Long actual = t.get(usageHistory.actualUsageTime.sumLong());
        Integer month = t.get(usageHistory.startAt.month());
        if (month == null) month = 1; // null이면 기본값 1월
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

    /* ====================
      유틸
    ==================== */
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
