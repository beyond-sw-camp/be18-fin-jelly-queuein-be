package com.beyond.qiin.domain.accounting.repository.querydsl;

import com.beyond.qiin.domain.accounting.dto.usage_history.response.raw.UsageHistoryTrendPopularCountDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.raw.UsageHistoryTrendPopularTimeDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.raw.UsageHistoryTrendRawDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.raw.UsageHistoryTrendRawDto.UsageAggregate;
import com.beyond.qiin.domain.accounting.entity.QUsageHistory;
import com.beyond.qiin.domain.accounting.exception.UsageHistoryException;
import com.beyond.qiin.domain.inventory.entity.QAsset;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UsageHistoryTrendQueryAdapterImpl implements UsageHistoryTrendQueryAdapter {

    private final JPAQueryFactory queryFactory;

    private final QUsageHistory u = QUsageHistory.usageHistory;
    private final QAsset a = QAsset.asset;

    @Override
    public UsageHistoryTrendRawDto getTrendData(int baseYear, int compareYear, String assetName) {

        AssetInfoResult info = resolveAsset(assetName);

        Map<Integer, UsageAggregate> base = getMonthlyUsage(baseYear, info.assetId());
        Map<Integer, UsageAggregate> compare = getMonthlyUsage(compareYear, info.assetId());

        return UsageHistoryTrendRawDto.builder()
                .assetId(info.assetId())
                .assetName(info.assetName())
                .baseYearData(base)
                .compareYearData(compare)
                .build();
    }

    @Override
    public List<UsageHistoryTrendPopularCountDto> getTopByCount(int year) {

        var countExpr = u.id.count();

        List<Tuple> rows = queryFactory
                .select(a.id, a.name, countExpr)
                .from(u)
                .join(u.asset, a)
                .where(u.startAt.year().eq(year))
                .groupBy(a.id, a.name)
                .orderBy(countExpr.desc())
                .limit(3)
                .fetch();

        return rows.stream()
                .map(r -> {
                    Long id = r.get(a.id);
                    String name = r.get(a.name);
                    Long countVal = r.get(countExpr);

                    return new UsageHistoryTrendPopularCountDto(id, name, countVal != null ? countVal.intValue() : 0);
                })
                .toList();
    }

    @Override
    public List<UsageHistoryTrendPopularTimeDto> getTopByTime(int year) {

        var sumExpr = u.usageTime.sumLong();

        List<Tuple> rows = queryFactory
                .select(a.id, a.name, sumExpr)
                .from(u)
                .join(u.asset, a)
                .where(u.startAt.year().eq(year))
                .groupBy(a.id, a.name)
                .orderBy(sumExpr.desc())
                .limit(3)
                .fetch();

        return rows.stream()
                .map(r -> {
                    Long id = r.get(a.id);
                    String name = r.get(a.name);
                    Long totalVal = r.get(sumExpr); // 값 1회 조회

                    int safeTotal = (totalVal != null) ? totalVal.intValue() : 0;

                    return new UsageHistoryTrendPopularTimeDto(id, name, safeTotal);
                })
                .toList();
    }

    private AssetInfoResult resolveAsset(String assetName) {

        if (assetName == null || assetName.isBlank()) {
            return new AssetInfoResult(null, "전체");
        }

        Tuple row = queryFactory
                .select(a.id, a.name)
                .from(a)
                .where(a.name.containsIgnoreCase(assetName))
                .limit(1)
                .fetchOne();

        if (row == null) {
            throw UsageHistoryException.invalidAssetName();
        }

        return new AssetInfoResult(row.get(a.id), row.get(a.name));
    }

    private Map<Integer, UsageAggregate> getMonthlyUsage(int year, Long assetId) {

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(u.startAt.year().eq(year));

        if (assetId != null) {
            builder.and(u.asset.id.eq(assetId));
        }

        var monthExpr = u.startAt.month();
        var actualExpr = u.actualUsageTime.sumLong();
        var reservedExpr = u.usageTime.sumLong();

        List<Tuple> rows = queryFactory
                .select(monthExpr, actualExpr, reservedExpr)
                .from(u)
                .join(u.asset, a)
                .where(builder)
                .groupBy(monthExpr)
                .fetch();

        Map<Integer, UsageAggregate> map = new HashMap<>();

        for (Tuple r : rows) {
            Integer month = r.get(monthExpr);
            Long actual = r.get(actualExpr);
            Long reserved = r.get(reservedExpr);

            map.put(
                    month,
                    UsageAggregate.builder()
                            .actualUsage(actual != null ? actual.intValue() : 0)
                            .reservedUsage(reserved != null ? reserved.intValue() : 0)
                            .build());
        }

        for (int i = 1; i <= 12; i++) {
            map.putIfAbsent(
                    i, UsageAggregate.builder().actualUsage(0).reservedUsage(0).build());
        }

        return map;
    }

    private record AssetInfoResult(Long assetId, String assetName) {}
}
