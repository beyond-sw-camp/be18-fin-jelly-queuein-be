package com.beyond.qiin.domain.accounting.repository.querydsl;

import com.beyond.qiin.domain.accounting.dto.usage_history.response.raw.UsageHistoryTrendRawDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.raw.UsageHistoryTrendRawDto.UsageAggregate;
import com.beyond.qiin.domain.accounting.entity.QUsageHistory;
import com.beyond.qiin.domain.accounting.exception.UsageHistoryException;
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
            Long ignoredAssetId,
            String assetName,
            int ignoredMonths) {

        // 자원명 → assetId + assetName + assetCount 조회
        AssetInfoResult info = resolveAsset(assetName);

        // 월별 Raw 조회
        Map<Integer, UsageAggregate> base = getMonthlyUsage(baseYear, info.assetId());
        Map<Integer, UsageAggregate> compare = getMonthlyUsage(compareYear, info.assetId());

        return UsageHistoryTrendRawDto.builder()
                .assetId(info.assetId())
                .assetName(info.assetName())
                .assetCount(info.assetCount())
                .baseYearData(base)
                .compareYearData(compare)
                .build();
    }

    // 자원 이름으로 assetId + assetName + assetCount 조회
    private AssetInfoResult resolveAsset(String assetName) {

        // 전체 조회
        if (assetName == null || assetName.isBlank()) {

            Long count = queryFactory
                    .select(a.id.count())
                    .from(a)
                    .fetchOne();

            return new AssetInfoResult(null, "전체",
                    count != null ? count.intValue() : 0);
        }

        // 개별 자원 검색
        Tuple result = queryFactory
                .select(a.id, a.name)
                .from(a)
                .where(a.name.containsIgnoreCase(assetName))
                .limit(1)
                .fetchOne();

        if (result == null) {
            throw UsageHistoryException.invalidAssetName();
        }

        Long assetId = result.get(a.id);
        String name = result.get(a.name);

        return new AssetInfoResult(assetId, name, 1);
    }

    // assetId 기준으로 월별 집계
    private Map<Integer, UsageAggregate> getMonthlyUsage(int year, Long assetId) {

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(u.startAt.year().eq(year));

        // 전체 조회일 때 조건 없음
        if (assetId != null) {
            builder.and(u.asset.id.eq(assetId));
        }

        var monthExpr = u.startAt.month();
        var actualSumExpr = u.actualUsageTime.sumLong();
        var reservedSumExpr = u.usageTime.sumLong();

        List<Tuple> rows = queryFactory
                .select(monthExpr, actualSumExpr, reservedSumExpr)
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

            map.put(
                    m,
                    UsageAggregate.builder()
                            .actualUsage(actual != null ? actual.intValue() : 0)
                            .reservedUsage(reserved != null ? reserved.intValue() : 0)
                            .build());
        }

        // 항상 1~12월 채움
        for (int m = 1; m <= 12; m++) {
            map.putIfAbsent(m,
                    UsageAggregate.builder().actualUsage(0).reservedUsage(0).build());
        }

        return map;
    }

    // assetCount 포함된 완전한 AssetInfoResult
    private record AssetInfoResult(Long assetId, String assetName, int assetCount) {}

}
