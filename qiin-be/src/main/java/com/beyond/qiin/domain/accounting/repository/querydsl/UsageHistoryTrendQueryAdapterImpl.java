package com.beyond.qiin.domain.accounting.repository.querydsl;

import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryTrendRawDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryTrendRawDto.UsageAggregate;
import com.beyond.qiin.domain.accounting.entity.QUsageHistory;
import com.beyond.qiin.domain.inventory.entity.QAsset;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class UsageHistoryTrendQueryAdapterImpl implements UsageHistoryTrendQueryAdapter {

    private final JPAQueryFactory queryFactory;

    private final QUsageHistory u = QUsageHistory.usageHistory;
    private final QAsset a = QAsset.asset;

    @Override
    public UsageHistoryTrendRawDto getTrendData(
            int baseYear,
            int compareYear,
            Long assetId,
            String assetName,
            int ignoredMonths
    ) {

        AssetInfoResult info = resolveAsset(assetId, assetName);

        Map<Integer, UsageAggregate> base = getMonthlyUsage(baseYear, assetId, assetName);
        Map<Integer, UsageAggregate> compare = getMonthlyUsage(compareYear, assetId, assetName);

        return UsageHistoryTrendRawDto.builder()
                .assetId(assetId)
                .assetName(info.assetName())
                .assetCount(info.assetCount())
                .baseYearData(base)
                .compareYearData(compare)
                .build();
    }

    private AssetInfoResult resolveAsset(Long assetId, String assetName) {

        if (assetId != null) {
            String name = queryFactory.select(a.name)
                    .from(a)
                    .where(a.id.eq(assetId))
                    .fetchOne();

            return new AssetInfoResult(name, 1);
        }

        if (assetName != null && !assetName.isBlank()) {
            List<String> names = queryFactory.select(a.name)
                    .from(a)
                    .where(a.name.contains(assetName))
                    .limit(1)
                    .fetch();

            return new AssetInfoResult(names.get(0), 1);
        }

        Long cnt = queryFactory.select(a.id.count()).from(a).fetchOne();
        return new AssetInfoResult("전체", cnt != null ? cnt.intValue() : 0);
    }

    private Map<Integer, UsageAggregate> getMonthlyUsage(
            int year, Long assetId, String assetName
    ) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(u.startAt.year().eq(year));

        if (assetId != null) builder.and(u.asset.id.eq(assetId));
        else if (assetName != null && !assetName.isBlank()) builder.and(u.asset.name.contains(assetName));

        // ✔ QueryDSL 7 정식 문법
        var monthExpr = u.startAt.month();
        var actualSumExpr = u.actualUsageTime.sumLong();
        var reservedSumExpr = u.usageTime.sumLong();

        List<Tuple> rows = queryFactory
                .select(
                        monthExpr,
                        actualSumExpr,
                        reservedSumExpr
                )
                .from(u)
                .join(u.asset, a)
                .where(builder)
                .groupBy(monthExpr)
                .fetch();

        Map<Integer, UsageAggregate> map = new HashMap<>();

        for (Tuple row : rows) {
            Integer m = row.get(monthExpr);
            Long actual = row.get(actualSumExpr);
            Long reserved = row.get(reservedSumExpr);

            map.put(m, UsageAggregate.builder()
                    .actualUsage(actual == null ? 0 : actual.intValue())
                    .reservedUsage(reserved == null ? 0 : reserved.intValue())
                    .build());
        }

        // ✔ 항상 1~12월 채움
        for (int m = 1; m <= 12; m++) {
            map.putIfAbsent(m, UsageAggregate.builder()
                    .actualUsage(0)
                    .reservedUsage(0)
                    .build());
        }

        return map;
    }

    private record AssetInfoResult(String assetName, int assetCount) {}
}
