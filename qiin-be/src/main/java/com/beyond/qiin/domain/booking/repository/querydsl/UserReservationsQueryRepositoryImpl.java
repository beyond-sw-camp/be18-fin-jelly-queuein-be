package com.beyond.qiin.domain.booking.repository.querydsl;

import com.beyond.qiin.domain.booking.dto.reservation.request.search_condition.GetUserReservationSearchCondition;
import com.beyond.qiin.domain.booking.dto.reservation.response.raw.RawUserReservationResponseDto;
import com.beyond.qiin.domain.booking.entity.QReservation;
import com.beyond.qiin.domain.booking.enums.ReservationStatus;
import com.beyond.qiin.domain.inventory.entity.QAsset;
import com.beyond.qiin.domain.inventory.entity.QAssetClosure;
import com.beyond.qiin.domain.inventory.entity.QCategory;
import com.beyond.qiin.domain.inventory.enums.AssetStatus;
import com.beyond.qiin.domain.inventory.enums.AssetType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
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

        // 자원 유형(int) - 변환된 값 사용
        if (condition.getAssetType() != null) {
            String raw = condition.getAssetType().trim();

            try {
                AssetType statusEnum = AssetType.valueOf(raw.toUpperCase());

                builder.and(asset.type.eq(statusEnum.getCode()));

            } catch (IllegalArgumentException ignored) {
            }
        }

        // 카테고리 이름 기반 검색 → Category 조인
        if (condition.getCategoryName() != null) {
            builder.and(category.name.eq(condition.getCategoryName()));
        }

        // 자원 상태 (assetStatus) 필터링
        if (condition.getAssetStatus() != null) {
            String raw = condition.getAssetStatus().trim();

            try {
                AssetStatus statusEnum = AssetStatus.valueOf(raw.toUpperCase());

                builder.and(asset.status.eq(statusEnum.getCode()));

            } catch (IllegalArgumentException ignored) {
            }
        }

        BooleanBuilder closureFilter = new BooleanBuilder();
        boolean useClosure = false;

        if (condition.getLayerZero() != null) {
            useClosure = true;
            closureFilter
                    .and(closure.depth.eq(0))
                    .and(closure.assetClosureId.ancestorId.eq(Long.parseLong(condition.getLayerZero())));
        }

        if (condition.getLayerOne() != null) {
            useClosure = true;
            closureFilter
                    .and(closure.depth.eq(1))
                    .and(closure.assetClosureId.ancestorId.eq(Long.parseLong(condition.getLayerOne())));
        }

        JPAQuery<RawUserReservationResponseDto> contentQuery = query.select(Projections.constructor(
                        RawUserReservationResponseDto.class,
                        reservation.id,
                        reservation.startAt,
                        reservation.endAt,
                        reservation.status,
                        reservation.isApproved,
                        reservation.actualStartAt,
                        reservation.actualEndAt,
                        reservation.version,
                        asset.id,
                        asset.name,
                        category.name.as("categoryName"),
                        asset.type,
                        asset.status))
                .from(reservation)
                .join(asset)
                .on(asset.id.eq(reservation.asset.id))
                .leftJoin(category)
                .on(category.id.eq(asset.category.id));

        if (useClosure) {
            contentQuery
                    .leftJoin(closure)
                    .on(closure.assetClosureId.descendantId.eq(asset.id))
                    .where(builder.and(closureFilter));
        } else {
            contentQuery.where(builder);
        }

        List<RawUserReservationResponseDto> content = contentQuery
                .orderBy(reservation.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> totalQuery = query.select(reservation.countDistinct())
                .from(reservation)
                .join(asset)
                .on(asset.id.eq(reservation.asset.id))
                .leftJoin(category)
                .on(category.id.eq(asset.category.id));

        if (useClosure) {
            totalQuery
                    .leftJoin(closure)
                    .on(closure.assetClosureId.descendantId.eq(asset.id))
                    .where(builder.and(closureFilter));
        } else {
            totalQuery.where(builder);
        }

        Long total = totalQuery.fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
