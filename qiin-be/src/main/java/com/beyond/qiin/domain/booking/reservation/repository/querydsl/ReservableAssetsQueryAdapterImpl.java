package com.beyond.qiin.domain.booking.reservation.repository.querydsl;

import com.beyond.qiin.domain.booking.dto.reservation.request.ReservableAssetSearchCondition;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservableAssetResponseDto;
import com.beyond.qiin.domain.inventory.entity.QAsset;
import com.beyond.qiin.domain.inventory.entity.QAssetClosure;
import com.beyond.qiin.domain.inventory.entity.QCategory;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

// TODO : is reservable을 어떻게 포함해줄지에 대해 고려

@Repository
@RequiredArgsConstructor
public class ReservableAssetsQueryAdapterImpl implements ReservableAssetsQueryAdapter {

    private final JPAQueryFactory query;

    private static final QAsset asset = QAsset.asset;
    private static final QReservation reservation = QReservation.reservation;
    private static final QAssetClosure closure = QAssetClosure.assetClosure;
    private static final QCategory category = QCategory.category;

    @Override
    public Page<ReservableAssetResponseDto> search(ReservableAssetSearchCondition condition, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        // 자원 관련
        // 이름 검색
        if (condition.getAssetName() != null) {
            builder.and(asset.name.containsIgnoreCase(condition.getAssetName()));
        }

        // 자원 유형
        if (condition.getAssetType() != null) {
            try {
                builder.and(asset.type.eq(Integer.parseInt(condition.getAssetType())));
            } catch (NumberFormatException ignored) {
            }
        }

        // 카테고리
        if (condition.getCategoryName() != null) {
            builder.and(category.name.eq(condition.getCategoryName()));
        }

        // 상태(사용가능 / 점검중 / 예약불가 등)
        if (condition.getAssetStatus() != null) {
            try {
                builder.and(asset.status.eq(Integer.parseInt(condition.getAssetStatus())));
            } catch (NumberFormatException ignored) {
            }
        }

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
        // 날짜 기반 예약 여부
        Instant date = condition.getDate();

        // 하루 범위
        Instant start = date;
        Instant end = date.plusSeconds(24 * 3600);

        List<ReservableAssetResponseDto> content = query.select(Projections.constructor(
                        ReservableAssetResponseDto.class,
                        asset.id,
                        asset.name,
                        asset.type,
                        category.name,
                        asset.status,
                        asset.needsApproval,
                        reservation.id.isNull() // 해당 시간대 예약이 없으면 true
                        ))
                .from(asset)
                .leftJoin(reservation)
                .on(reservation.assetId.eq(asset.id).and(reservation.startAt.between(start, end)))
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(asset.name.asc())
                .fetch();

        long total = query.select(asset.count()).from(asset).where(builder).fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
