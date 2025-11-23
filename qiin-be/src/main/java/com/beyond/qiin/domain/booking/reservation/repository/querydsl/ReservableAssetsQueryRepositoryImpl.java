package com.beyond.qiin.domain.booking.reservation.repository.querydsl;

import com.beyond.qiin.domain.booking.dto.reservation.request.search_condition.ReservableAssetSearchCondition;
import com.beyond.qiin.domain.booking.dto.reservation.response.raw.RawReservableAssetResponseDto;
import com.beyond.qiin.domain.booking.reservation.entity.QReservation;
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

// TODO : is reservable을 어떻게 포함해줄지에 대해 고려

@Repository
@RequiredArgsConstructor
public class ReservableAssetsQueryRepositoryImpl implements ReservableAssetsQueryRepository {

    private final JPAQueryFactory query;

    private static final QAsset asset = QAsset.asset;
    private static final QReservation reservation = QReservation.reservation;
    private static final QAssetClosure closure = QAssetClosure.assetClosure;
    private static final QCategory category = QCategory.category;

    @Override
    public Page<RawReservableAssetResponseDto> search(ReservableAssetSearchCondition condition, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        Instant start = null;
        Instant end = null;

        // 날짜 조건
        if (condition.getDate() != null) {
            LocalDate date = condition.getDate().atZone(ZoneId.of("Asia/Seoul")).toLocalDate();

            start = date.atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant();
            end = date.plusDays(1).atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant();

            builder.and(reservation.startAt.between(start, end));
        }

        // 자원 관련
        // 이름 검색
        if (condition.getAssetName() != null) {
            builder.and(asset.name.containsIgnoreCase(condition.getAssetName()));
        }

        // 자원 유형
        //        if (condition.getAssetType() != null) {
        //            try {
        //                builder.and(asset.type.eq(assetType));
        //            } catch (NumberFormatException ignored) {
        //            }
        //        }

        // 카테고리
        if (condition.getCategoryName() != null) {
            builder.and(category.name.eq(condition.getCategoryName()));
        }

        // 상태(사용가능 / 점검중 / 예약불가 등)
        //        if (condition.getAssetStatus() != null) {
        //            try {
        //                builder.and(asset.status.eq(assetStatus));
        //            } catch (NumberFormatException ignored) {
        //            }
        //        }

        BooleanBuilder closureOn = new BooleanBuilder();
        closureOn.and(closure.assetClosureId.descendantId.eq(asset.id));

        // 0계층 / 1계층
        if (condition.getLayerZero() != null) {
            builder.and(closure.depth
                    .eq(0)
                    .and(closure.assetClosureId.descendantId.eq(asset.id))
                    .and(closure.assetClosureId.ancestorId.eq(Long.parseLong(condition.getLayerZero()))));
        }

        if (condition.getLayerOne() != null) {
            builder.and(closure.depth
                    .eq(1)
                    .and(closure.assetClosureId.descendantId.eq(asset.id))
                    .and(closure.assetClosureId.ancestorId.eq(Long.parseLong(condition.getLayerOne()))));
        }

        List<RawReservableAssetResponseDto> content = query.select(Projections.constructor(
                        RawReservableAssetResponseDto.class,
                        asset.id,
                        asset.name,
                        //                        asset.type,
                        category.name,
                        //                        asset.status,
                        asset.needsApproval,
                        reservation.status
                ))

                .from(reservation)
                .join(asset)
                .on(asset.id.eq(reservation.asset.id))
                .leftJoin(category)
                .on(category.id.eq(asset.categoryId))
                .leftJoin(closure)
                .on(closureOn)
                .where(builder)
                .orderBy(getOrderSpecifiers(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = query.select(reservation.count())
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
