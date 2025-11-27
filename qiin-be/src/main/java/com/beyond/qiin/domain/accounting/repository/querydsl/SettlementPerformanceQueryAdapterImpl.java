package com.beyond.qiin.domain.accounting.repository.querydsl;

import static com.beyond.qiin.domain.accounting.entity.QSettlement.settlement;
import static com.beyond.qiin.domain.inventory.entity.QAsset.asset;

import com.beyond.qiin.domain.accounting.dto.settlement.response.raw.SettlementPerformanceRawDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SettlementPerformanceQueryAdapterImpl implements SettlementPerformanceQueryAdapter {

    private final JPAQueryFactory queryFactory;

    @Override
    public SettlementPerformanceRawDto getMonthlyPerformance(
            int baseYear, int compareYear, Long assetId, String assetName) {

        // --- 검색 검증 ------------------------------------------------
        if (assetId == null && assetName != null && !assetName.isBlank()) {
            validateAssetNameOrThrow(assetName);
        }

        // --- 1) 기준연도 월별 SUM -------------------------------------
        Map<Integer, BigDecimal> base = getMonthlySum(baseYear, assetId, assetName);

        // --- 2) 비교연도 월별 SUM -------------------------------------
        Map<Integer, BigDecimal> compare = getMonthlySum(compareYear, assetId, assetName);

        // --- 3) 화면용 asset 이름 결정 --------------------------------
        String resolvedName = resolveAssetName(assetId, assetName);

        return SettlementPerformanceRawDto.builder()
                .assetId(assetId)
                .assetName(resolvedName)
                .baseYearData(base)
                .compareYearData(compare)
                .build();
    }

    // -----------------------------------------------------------------------
    // 월별 usage_gap_cost SUM
    // -----------------------------------------------------------------------
    private Map<Integer, BigDecimal> getMonthlySum(int year, Long assetId, String assetName) {

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(settlement.createdAt.year().eq(year)); // 정산 생성년도 기준

        if (assetId != null) {
            builder.and(settlement.asset.id.eq(assetId));

        } else if (assetName != null && !assetName.isBlank()) {
            builder.and(settlement.asset.name.containsIgnoreCase(assetName));
        }

        var monthExpr = settlement.createdAt.month();
        var sumExpr = settlement.usageGapCost.sumBigDecimal().coalesce(BigDecimal.ZERO);

        List<Tuple> rows = queryFactory
                .select(monthExpr, sumExpr)
                .from(settlement)
                .join(settlement.asset, asset)
                .where(builder)
                .groupBy(monthExpr)
                .fetch();

        Map<Integer, BigDecimal> result = new HashMap<>();

        for (Tuple t : rows) {
            Integer m = t.get(monthExpr);
            BigDecimal sum = t.get(sumExpr);

            result.put(m, sum == null ? BigDecimal.ZERO : sum);
        }

        // ✔ 1~12월 누락된 달을 0으로 채움
        for (int m = 1; m <= 12; m++) {
            result.putIfAbsent(m, BigDecimal.ZERO);
        }

        return result;
    }

    // -----------------------------------------------------------------------
    // asset 이름 검증 (없는 자원명 검색 금지)
    // -----------------------------------------------------------------------
    private void validateAssetNameOrThrow(String assetName) {

        Long count = queryFactory
                .select(asset.id.count())
                .from(asset)
                .where(asset.name.containsIgnoreCase(assetName))
                .fetchOne();

        if (count == null || count == 0) {
            throw new IllegalArgumentException("존재하지 않는 자원명입니다: " + assetName);
        }
    }

    // -----------------------------------------------------------------------
    // asset 이름 결정 로직 (전체 / 단일 / 이름 검색)
    // -----------------------------------------------------------------------
    private String resolveAssetName(Long assetId, String assetName) {

        if (assetId != null) {
            return queryFactory
                    .select(asset.name)
                    .from(asset)
                    .where(asset.id.eq(assetId))
                    .fetchOne();
        }

        if (assetName != null && !assetName.isBlank()) {

            return queryFactory
                    .select(asset.name)
                    .from(asset)
                    .where(asset.name.containsIgnoreCase(assetName))
                    .limit(1)
                    .fetchOne(); // 실제 존재하는 이름만 반환
        }

        return "전체";
    }
}
