package com.beyond.qiin.domain.booking.reservation.repository.querydsl;

import com.beyond.qiin.domain.booking.dto.reservation.request.search_condition.GetUserReservationSearchCondition;
import com.beyond.qiin.domain.booking.dto.reservation.response.GetUserReservationResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.RawUserReservationResponseDto;
import com.beyond.qiin.domain.booking.reservation.entity.QReservation;
import com.beyond.qiin.domain.booking.reservation.enums.ReservationStatus;
import com.beyond.qiin.domain.inventory.entity.QAsset;
import com.beyond.qiin.domain.inventory.entity.QAssetClosure;
import com.beyond.qiin.domain.inventory.entity.QCategory;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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

        // 사용자 예약 필터 : 입력받은 userId 기준으로
        builder.and(reservation.applicant.id.eq(userId));

        // 날짜(Instant)
        if (condition.getDate() != null) {
            Instant start = condition.getDate();
            Instant end = start.plus(1, ChronoUnit.DAYS);
            builder.and(reservation.startAt.between(start, end));
        }

        // 예약 상태 (int)
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

        // 0계층 / 1계층 (AssetClosure 기반)
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

        List<RawUserReservationResponseDto> content = query.select(Projections.constructor(
                        RawUserReservationResponseDto.class,
                        reservation.id,
                        reservation.startAt,
                        reservation.endAt,
                        reservation.status,
                        reservation.isApproved,
                        asset.id,
                        asset.name,
                        category.name.as("categoryName"),
                        asset.type,
                        asset.status))
                .from(reservation)
                .join(asset)
                .on(asset.id.eq(reservation.asset.id))
                .leftJoin(category)
                .on(category.id.eq(asset.categoryId))
                .leftJoin(closure)
                .on(closure.assetClosureId.descendantId.eq(asset.id))
                .where(builder)
                .orderBy(reservation.startAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = query.select(reservation.count())
                .from(reservation)
                .join(asset)
                .on(asset.id.eq(reservation.asset.id))
                .leftJoin(category)
                .on(category.id.eq(asset.categoryId))
                .leftJoin(closure)
                .on(closure.assetClosureId.descendantId.eq(asset.id))
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
