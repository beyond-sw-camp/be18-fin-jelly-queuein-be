package com.beyond.qiin.domain.accounting.repository.querydsl;

import static com.beyond.qiin.domain.accounting.entity.QSettlement.settlement;
import static com.beyond.qiin.domain.accounting.entity.QUsageHistory.usageHistory;
import static com.beyond.qiin.domain.accounting.entity.QUserHistory.userHistory;
import static com.beyond.qiin.domain.booking.entity.QReservation.reservation;
import static com.beyond.qiin.domain.iam.entity.QUser.user;
import static com.beyond.qiin.domain.inventory.entity.QAsset.asset;

import com.beyond.qiin.domain.accounting.dto.usage_history.request.UsageHistoryListSearchRequestDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryDetailResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryListResponseDto;
import com.beyond.qiin.domain.accounting.exception.UsageHistoryException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.time.ZoneId;
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
    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    @Override
    public Page<UsageHistoryListResponseDto> searchUsageHistory(
            UsageHistoryListSearchRequestDto req, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();

        /* ================================
           날짜 검색 (LocalDate → Instant 변환)
        ================================ */
        if (req.getStartDate() != null) {
            Instant startInstant = req.getStartDate()
                    .atStartOfDay(ZONE) // 2025-09-25 00:00 KST
                    .toInstant(); // UTC 로 변환
            builder.and(usageHistory.startAt.goe(startInstant));
        }

        if (req.getEndDate() != null) {
            Instant endInstant = req.getEndDate()
                    .plusDays(1) // 다음날 00:00 (해당일 전부 포함)
                    .atStartOfDay(ZONE)
                    .toInstant();
            builder.and(usageHistory.endAt.lt(endInstant));
        }

        /* ================================
           키워드 검색 (자원명 or 예약자명)
        ================================ */
        if (req.getKeyword() != null && !req.getKeyword().isBlank()) {
            String keyword = req.getKeyword();
            builder.and(asset.name.containsIgnoreCase(keyword).or(user.userName.containsIgnoreCase(keyword)));
        }

        /* ================================
           메인 조회
        ================================ */
        List<UsageHistoryListResponseDto> result = queryFactory
                .select(Projections.constructor(
                        UsageHistoryListResponseDto.class,
                        usageHistory.id,
                        asset.name,
                        usageHistory.startAt,
                        usageHistory.endAt,
                        usageHistory.usageTime,
                        usageHistory.actualStartAt,
                        usageHistory.actualEndAt,
                        usageHistory.actualUsageTime,
                        usageHistory.usageRatio))
                .from(usageHistory)
                .join(usageHistory.asset, asset)
                .leftJoin(usageHistory.reservation, reservation)
                .leftJoin(userHistory)
                .on(userHistory.usageHistory.eq(usageHistory))
                .leftJoin(userHistory.user, user)
                .where(builder)
                .orderBy(usageHistory.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        /* ================================
           Count 조회
        ================================ */
        Long count = queryFactory
                .select(usageHistory.count())
                .from(usageHistory)
                .join(usageHistory.asset, asset)
                .leftJoin(usageHistory.reservation, reservation)
                .leftJoin(userHistory)
                .on(userHistory.usageHistory.eq(usageHistory))
                .leftJoin(userHistory.user, user)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(result, pageable, count == null ? 0 : count);
    }

    @Override
    public UsageHistoryDetailResponseDto getUsageHistoryDetail(final Long usageHistoryId) {

        UsageHistoryDetailResponseDto base = queryFactory
                .select(Projections.constructor(
                        UsageHistoryDetailResponseDto.class,
                        usageHistory.id,
                        asset.name,
                        Expressions.nullExpression(String.class),
                        Expressions.nullExpression(List.class),
                        settlement.totalUsageCost,
                        settlement.actualUsageCost))
                .from(usageHistory)
                .join(usageHistory.asset, asset)
                .leftJoin(settlement)
                .on(settlement.usageHistory.eq(usageHistory))
                .where(usageHistory.id.eq(usageHistoryId))
                .fetchOne();

        if (base == null) {
            throw UsageHistoryException.notFound();
        }

        // 참여자 조회
        List<String> names = queryFactory
                .select(user.userName)
                .from(userHistory)
                .join(userHistory.user, user)
                .where(userHistory.usageHistory.id.eq(usageHistoryId))
                .fetch();

        return UsageHistoryDetailResponseDto.builder()
                .usageHistoryId(base.getUsageHistoryId())
                .assetName(base.getAssetName())
                .assetImage(base.getAssetImage())
                .reserverNames(names)
                .billAmount(base.getBillAmount())
                .actualBillAmount(base.getActualBillAmount())
                .build();
    }
}
