package com.beyond.qiin.domain.booking.repository.querydsl;

import com.beyond.qiin.domain.booking.dto.reservation.request.search_condition.GetAppliedReservationSearchCondition;
import com.beyond.qiin.domain.booking.dto.reservation.response.raw.RawAppliedReservationResponseDto;
import com.beyond.qiin.domain.booking.entity.QReservation;
import com.beyond.qiin.domain.iam.entity.QUser;
import com.beyond.qiin.domain.inventory.entity.QAsset;
import com.beyond.qiin.domain.inventory.entity.QAssetClosure;
import com.beyond.qiin.domain.inventory.entity.QCategory;
import com.beyond.qiin.domain.inventory.enums.AssetStatus;
import com.beyond.qiin.domain.inventory.enums.AssetType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AppliedReservationsQueryRepositoryImpl implements AppliedReservationsQueryRepository {

    private final JPAQueryFactory query;

    private static final QReservation reservation = QReservation.reservation;
    private static final QAsset asset = QAsset.asset;
    private static final QCategory category = QCategory.category;
    private static final QAssetClosure closure = QAssetClosure.assetClosure;
    private static final QUser applicant = new QUser("applicant");
    private static final QUser respondent = new QUser("respondent");

    @Override
    public List<RawAppliedReservationResponseDto> search(GetAppliedReservationSearchCondition condition) {

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(reservation.isApplied.eq(true)); // 신청된 경우

        // 날짜(Instant)
        if (condition.getDate() != null) {
            LocalDate date = condition.getDate();

            Instant start = date.atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant();
            Instant end = date.plusDays(1).atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant();

            builder.and(reservation.startAt.between(start, end));
        }

        // 신청자 이름 검색
        if (condition.getApplicantName() != null) {
            builder.and(applicant.userName.containsIgnoreCase(condition.getApplicantName()));
        }

        // 승인자 이름 검색
        if (condition.getRespondentName() != null) {
            builder.and(respondent.userName.containsIgnoreCase(condition.getRespondentName()));
        }

        // 승인 여부
        if (condition.getIsApproved() != null) {
            boolean approved = Boolean.parseBoolean(condition.getIsApproved());
            builder.and(reservation.isApproved.eq(approved));
        }

        // 자원명
        if (condition.getAssetName() != null) {
            builder.and(asset.name.containsIgnoreCase(condition.getAssetName()));
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

        if (condition.getCategoryId() != null) {
            builder.and(asset.category.id.eq(condition.getCategoryId()));
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

        BooleanBuilder closureFilter = new BooleanBuilder();
        boolean needsClosureJoin = false;

        if (condition.getLayerOne() != null) {
            needsClosureJoin = true;
            builder.and(closure.assetClosureId.ancestorId.eq(Long.valueOf(condition.getLayerOne())))
                    .and(closure.depth.gt(0)); // 자기 자신 제외
        } else if (condition.getLayerZero() != null) {
            needsClosureJoin = true;
            builder.and(closure.assetClosureId.ancestorId.eq(Long.valueOf(condition.getLayerZero())))
                    .and(closure.depth.gt(0)); // 자기 자신 제외
        }

        // 조회
        var queryBuilder = query.select(Projections.constructor(
                        RawAppliedReservationResponseDto.class,
                        asset.id,
                        asset.name,
                        reservation.id,
                        applicant.userName,
                        respondent.userName,
                        reservation.status,
                        reservation.isApproved,
                        reservation.reason,
                        reservation.version,
                        reservation.startAt,
                        reservation.endAt))
                .from(reservation)
                .join(reservation.asset, asset)
                .leftJoin(asset.category, category)
                .leftJoin(reservation.applicant, applicant)
                .leftJoin(reservation.respondent, respondent);

        if (needsClosureJoin) {
            queryBuilder.leftJoin(closure).on(closure.assetClosureId.descendantId.eq(asset.id));
        }

        return queryBuilder.where(builder).orderBy(reservation.id.desc()).fetch();
    }
}
