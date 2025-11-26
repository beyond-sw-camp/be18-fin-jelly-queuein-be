package com.beyond.qiin.domain.booking.repository.querydsl;

import com.beyond.qiin.domain.booking.dto.reservation.request.search_condition.GetUserReservationSearchCondition;
import com.beyond.qiin.domain.booking.dto.reservation.response.raw.RawUserReservationResponseDto;
import com.beyond.qiin.domain.booking.entity.QReservation;
import com.beyond.qiin.domain.booking.enums.ReservationStatus;
import com.beyond.qiin.domain.inventory.entity.QAsset;
import com.beyond.qiin.domain.inventory.entity.QAssetClosure;
import com.beyond.qiin.domain.inventory.entity.QCategory;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserReservationsQueryRepositoryImpl implements UserReservationsQueryRepository {
    private final JPAQueryFactory query;

    private static final QReservation reservation = QReservation.reservation;
    private static final QAsset asset = QAsset.asset;
    private static final QCategory category = QCategory.category;
    private static final QAssetClosure closure = QAssetClosure.assetClosure;

    @Override
    public Page<RawUserReservationResponseDto> search(
            Long userId, GetUserReservationSearchCondition condition, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();

        // 사용자
        builder.and(reservation.applicant.id.eq(userId));

        // 날짜(Instant)
        if (condition.getDate() != null) {
            LocalDate date = condition.getDate();

            Instant start = date.atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant();
            Instant end = date.plusDays(1).atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant();

            builder.and(reservation.startAt.between(start, end));
        }

        // TODO 예약 상태 (int)
        if (condition.getReservationStatus() != null) {
            String raw = condition.getReservationStatus().trim();

            try {
                // "pending" → "PENDING" → ReservationStatus.PENDING
                ReservationStatus statusEnum = ReservationStatus.valueOf(raw.toUpperCase());

                // QueryDSL은 int 비교
                builder.and(reservation.status.eq(statusEnum.getCode()));

            } catch (IllegalArgumentException ignored) {
                // 잘못된 enum 문자열이 들어오면 필터 적용 안 함
            }
        }

        // 승인 여부 (true/false)
        if (condition.getIsApproved() != null) {
            boolean isApproved = Boolean.parseBoolean(condition.getIsApproved());
            builder.and(reservation.isApproved.eq(isApproved));
        }

        // 자원명 검색
        if (condition.getAssetName() != null) {
            builder.and(asset.name.containsIgnoreCase(condition.getAssetName()));
        }

        // 자원 유형(int)
        if (condition.getAssetType() != null) {
            try {
                builder.and(asset.type.eq(Integer.parseInt(condition.getAssetType())));
            } catch (NumberFormatException ignored) {
            }
        }

        // 카테고리 이름 기반 검색 → Category 조인
        if (condition.getCategoryName() != null) {
            builder.and(category.name.eq(condition.getCategoryName()));
        }

        // 자원 상태(int)
        if (condition.getAssetStatus() != null) {
            try {
                builder.and(asset.status.eq(Integer.parseInt(condition.getAssetStatus())));
            } catch (NumberFormatException ignored) {
            }
        }
        BooleanBuilder closureOn = new BooleanBuilder();
        closureOn.and(closure.assetClosureId.descendantId.eq(asset.id));

        // 0계층 / 1계층 (AssetClosure 기반)
        if (condition.getLayerZero() != null) {
            closureOn
                    .and(closure.depth.eq(0))
                    .and(closure.assetClosureId.ancestorId.eq(Long.parseLong(condition.getLayerZero())));
        }

        if (condition.getLayerOne() != null) {
            closureOn
                    .and(closure.depth.eq(1))
                    .and(closure.assetClosureId.ancestorId.eq(Long.parseLong(condition.getLayerOne())));
        }

        List<RawUserReservationResponseDto> content = query.select(Projections.constructor(
                        RawUserReservationResponseDto.class,
                        reservation.id,
                        reservation.startAt,
                        reservation.endAt,
                        reservation.status,
                        reservation.isApproved,
                        reservation.actualStartAt,
                        reservation.actualEndAt,
                        asset.id,
                        asset.name,
                        category.name.as("categoryName"),
                        asset.type,
                        asset.status))
                .from(reservation)
                .join(asset)
                .on(asset.id.eq(reservation.asset.id))
                .leftJoin(category)
                .on(category.id.eq(asset.category.id))
                .leftJoin(closure)
                .on(closureOn)
                .where(builder)
                .orderBy(getOrderSpecifiers(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = query.select(reservation.count())
                .from(reservation)
                .join(asset)
                .on(asset.id.eq(reservation.asset.id))
                .leftJoin(closure)
                .on(closureOn)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Pageable pageable) {
        return pageable.getSort().stream()
                .map(order -> {
                    String property = order.getProperty(); // "startAt", "status" ...

                    Order direction = order.isAscending() ? Order.ASC : Order.DESC;

                    PathBuilder<?> path = new PathBuilder<>(QReservation.class, "reservation");

                    return new OrderSpecifier(direction, path.get(property));
                })
                .toArray(OrderSpecifier[]::new);
    }
}
