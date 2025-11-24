package com.beyond.qiin.domain.accounting.repository.querydsl;

import static com.beyond.qiin.domain.accounting.entity.QSettlement.settlement;
import static com.beyond.qiin.domain.accounting.entity.QUsageHistory.usageHistory;
import static com.beyond.qiin.domain.accounting.entity.QUserHistory.userHistory;
import static com.beyond.qiin.domain.iam.entity.QUser.user;
import static com.beyond.qiin.domain.inventory.entity.QAsset.asset;

import com.beyond.qiin.domain.accounting.dto.usage_history.request.UsageHistorySearchRequestDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryDetailResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryListResponseDto;
import com.beyond.qiin.domain.accounting.exception.UsageHistoryException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class UsageHistoryQueryAdapterImpl implements UsageHistoryQueryAdapter {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<UsageHistoryListResponseDto> searchUsageHistory(
            final UsageHistorySearchRequestDto req, final Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        Instant start = req.getStartDate();
        Instant end = req.getEndDate();
        String keyword = req.getKeyword();

        if (start != null) {
            builder.and(usageHistory.endAt.goe(start));
        }

        if (end != null) {
            builder.and(usageHistory.startAt.loe(end));
        }

        if (StringUtils.hasText(keyword)) {
            keyword = keyword.trim();
            builder.and(asset.name.containsIgnoreCase(keyword));
        }

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
                .join(asset)
                .on(asset.id.eq(usageHistory.assetId))
                .where(builder)
                .fetchOne();

        return new PageImpl<>(items, pageable, total == null ? 0 : total);
    }

    @Override
    public UsageHistoryDetailResponseDto getUsageHistoryDetail(final Long usageHistoryId) {

        // 1) 기본 정보 + 정산 정보 + 자원명 조회
        UsageHistoryDetailResponseDto base = queryFactory
                .select(Projections.constructor(
                        UsageHistoryDetailResponseDto.class,
                        usageHistory.id,
                        asset.name, // assetName
                        Expressions.nullExpression(String.class), // assetImage (추후 필요하면 조인)
                        Expressions.nullExpression(List.class), // reserverNames -> 아래에서 채움
                        settlement.usageCost, // billAmount (예약 기준 금액)
                        settlement.totalUsageCost, // actualBillAmount (실제 사용 기준)
                        settlement.periodCostShare // fixedCost
                        ))
                .from(usageHistory)
                .join(asset)
                .on(asset.id.eq(usageHistory.assetId))
                .leftJoin(settlement)
                .on(settlement.usageHistoryId.eq(usageHistory.id))
                .where(usageHistory.id.eq(usageHistoryId))
                .fetchOne();

        if (base == null) {
            throw UsageHistoryException.notFound();
        }

        // 2) 참여자 이름 조회 (최대 3명)
        List<String> names = queryFactory
                .select(user.userName)
                .from(userHistory)
                .join(user)
                .on(user.id.eq(userHistory.userId))
                .where(userHistory.usageHistoryId.eq(usageHistoryId))
                .fetch();

        // 3) 최종 DTO 재구성
        return UsageHistoryDetailResponseDto.builder()
                .usageHistoryId(base.getUsageHistoryId())
                .assetName(base.getAssetName())
                .assetImage(base.getAssetImage())
                .reserverNames(names)
                .billAmount(base.getBillAmount())
                .actualBillAmount(base.getActualBillAmount())
                .fixedCost(base.getFixedCost())
                .build();
    }
}
