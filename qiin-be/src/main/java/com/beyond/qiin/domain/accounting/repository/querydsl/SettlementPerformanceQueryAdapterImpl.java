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

        // 기준연도 월별 SUM
        Map<Integer, BigDecimal> base = getMonthlySum(baseYear, assetId);

        // 비교연도 월별 SUM
        Map<Integer, BigDecimal> compare = getMonthlySum(compareYear, assetId);

        // 화면용 Asset Name
        String resolvedName = resolveAssetName(assetId); // assetId 기준으로 Asset Name을 찾음

        return SettlementPerformanceRawDto.builder()
                .assetId(assetId) // assetId로 저장
                .assetName(resolvedName) // assetName은 UI 용
                .baseYearData(base)
                .compareYearData(compare)
                .build();
    }

    //  누적 절감 금액 조회 (전체)
    @Override
    public BigDecimal getTotalSavingAllTime() {
        BigDecimal sum = queryFactory
                .select(settlement.usageGapCost.sumBigDecimal())
                .from(settlement)
                .fetchOne();

        return sum == null ? BigDecimal.ZERO : sum;
    }

    // 월별 SUM
    private Map<Integer, BigDecimal> getMonthlySum(int year, Long assetId) {

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(settlement.createdAt.year().eq(year)); // 정산 생성년도 기준

        // assetId만 사용하여 조회
        if (assetId != null) {
            builder.and(settlement.asset.id.eq(assetId));
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

        // 1~12월 값 보정
        for (int m = 1; m <= 12; m++) {
            result.putIfAbsent(m, BigDecimal.ZERO);
        }

        return result;
    }

    // 자원명을 기준으로 자원 ID를 찾기
    public Long getAssetIdByName(String assetName) {
        if (assetName == null || assetName.isBlank()) {
            return null; // "전체"를 의미함
        }

        return queryFactory
                .select(asset.id)
                .from(asset)
                .where(asset.name.containsIgnoreCase(assetName))
                .fetchOne();
    }

    // 자원 ID를 기준으로 자원명 조회
    private String resolveAssetName(Long assetId) {
        if (assetId != null) {
            return queryFactory
                    .select(asset.name)
                    .from(asset)
                    .where(asset.id.eq(assetId))
                    .fetchOne();
        }
        return "전체";
    }
}
