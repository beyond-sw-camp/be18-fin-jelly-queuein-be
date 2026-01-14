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
import com.querydsl.jpa.JPAExpressions;
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

    /* ============================================================
       자원 사용 기록 목록 조회 (N+1 / 중복 제거)
    ============================================================ */
    @Override
    public Page<UsageHistoryListResponseDto> searchUsageHistory(
            UsageHistoryListSearchRequestDto req, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();

        /* 날짜 검색 */
        if (req.getStartDate() != null) {
            Instant start = req.getStartDate().atStartOfDay(ZONE).toInstant();
            builder.and(usageHistory.startAt.goe(start));
        }

        if (req.getEndDate() != null) {
            Instant end = req.getEndDate().plusDays(1).atStartOfDay(ZONE).toInstant();
            builder.and(usageHistory.endAt.lt(end));
        }

        /* 키워드 검색 (자원명 OR 예약자명)
           - userHistory 직접 JOIN ❌
           - EXISTS 서브쿼리 ⭕
        */
        if (req.getKeyword() != null && !req.getKeyword().isBlank()) {
            String keyword = req.getKeyword();

            builder.and(asset.name
                    .containsIgnoreCase(keyword)
                    .or(JPAExpressions.selectOne()
                            .from(userHistory)
                            .join(userHistory.user, user)
                            .where(userHistory.usageHistory.eq(usageHistory), user.userName.containsIgnoreCase(keyword))
                            .exists()));
        }

        /* ================================
           리스트 조회
        ================================ */
        List<UsageHistoryListResponseDto> content = queryFactory
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
                .where(builder)
                .orderBy(usageHistory.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        /* ================================
           COUNT 조회 (JOIN 최소화)
        ================================ */
        Long total = queryFactory
                .select(usageHistory.count())
                .from(usageHistory)
                .join(usageHistory.asset, asset)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    /* ============================================================
       자원 사용 기록 상세 조회 (정상 동작)
    ============================================================ */
    @Override
    public UsageHistoryDetailResponseDto getUsageHistoryDetail(final Long usageHistoryId) {

        UsageHistoryDetailResponseDto base = queryFactory
                .select(Projections.constructor(
                        UsageHistoryDetailResponseDto.class,
                        usageHistory.id,
                        asset.name,
                        Expressions.nullExpression(String.class), // assetImage
                        Expressions.nullExpression(List.class), // reserverNames
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

        /* 참여자 목록 (별도 쿼리, 의도된 구조) */
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
