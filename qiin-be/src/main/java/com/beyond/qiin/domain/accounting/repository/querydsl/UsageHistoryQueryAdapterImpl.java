package com.beyond.qiin.domain.accounting.repository.querydsl;

import static com.beyond.qiin.domain.accounting.entity.QUsageHistory.usageHistory;
import static com.beyond.qiin.domain.inventory.entity.QAsset.asset;

import com.beyond.qiin.domain.accounting.dto.usage_history.request.UsageHistorySearchRequestDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryDetailResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryListResponseDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UsageHistoryQueryAdapterImpl implements UsageHistoryQueryAdapter {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<UsageHistoryListResponseDto> searchUsageHistory(
            final UsageHistorySearchRequestDto req, final Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (req.getStartDate() != null) builder.and(usageHistory.startAt.goe(req.getStartDate()));

        if (req.getEndDate() != null) builder.and(usageHistory.endAt.loe(req.getEndDate()));

        if (req.getKeyword() != null && !req.getKeyword().isBlank()) builder.and(asset.name.contains(req.getKeyword()));

        List<UsageHistoryListResponseDto> items = queryFactory
                .select(Projections.constructor(
                        UsageHistoryListResponseDto.class,
                        usageHistory.id,
                        asset.name,
                        usageHistory.startAt,
                        usageHistory.endAt,
                        usageHistory.usageTime,
                        Expressions.nullExpression(String.class),
                        usageHistory.actualStartAt,
                        usageHistory.actualEndAt,
                        usageHistory.actualUsageTime,
                        Expressions.nullExpression(String.class),
                        usageHistory.usageRatio,
                        Expressions.nullExpression(String.class)))
                .from(usageHistory)
                .join(asset)
                .on(asset.id.eq(usageHistory.assetId))
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(usageHistory.createdAt.desc(), usageHistory.id.desc())
                .fetch();

        Long total = queryFactory
                .select(usageHistory.count())
                .from(usageHistory)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(items, pageable, total == null ? 0 : total);
    }

    @Override
    public UsageHistoryDetailResponseDto getUsageHistoryDetail(final Long usageHistoryId) {

        return queryFactory
                .select(Projections.constructor(
                        UsageHistoryDetailResponseDto.class,
                        usageHistory.id,
                        asset.name,
                        Expressions.nullExpression(String.class),
                        Expressions.nullExpression(List.class),
                        usageHistory.usageRatio,
                        usageHistory.usageRatio,
                        usageHistory.usageRatio))
                .from(usageHistory)
                .join(asset)
                .on(asset.id.eq(usageHistory.assetId))
                .where(usageHistory.id.eq(usageHistoryId))
                .fetchOne();
    }
}
