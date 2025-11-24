package com.beyond.qiin.domain.booking.reservation.repository.querydsl;

import com.beyond.qiin.domain.booking.dto.reservation.request.search_condition.GetAppliedReservationSearchCondition;
import com.beyond.qiin.domain.booking.dto.reservation.response.raw.RawAppliedReservationResponseDto;
import com.beyond.qiin.domain.booking.reservation.entity.QReservation;
import com.beyond.qiin.domain.iam.entity.QUser;
import com.beyond.qiin.domain.inventory.entity.QAsset;
import com.beyond.qiin.domain.inventory.entity.QAssetClosure;
import com.beyond.qiin.domain.inventory.entity.QCategory;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
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
public class AppliedReservationsQueryRepositoryImpl implements AppliedReservationsQueryRepository {

    private final JPAQueryFactory query;

    private static final QReservation reservation = QReservation.reservation;
    private static final QAsset asset = QAsset.asset;
    private static final QCategory category = QCategory.category;
    private static final QAssetClosure closure = QAssetClosure.assetClosure;
    private static final QUser applicant = new QUser("applicant");
    private static final QUser respondent = new QUser("respondent");

    @Override
    public List<RawAppliedReservationResponseDto> search(
            GetAppliedReservationSearchCondition condition) {

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(reservation.status.eq(0)); // pending인 경우 == 신청된 reservations

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

        // 자원 유형(int)
        //        if (condition.getAssetType() != null) {
        //            try {
        //                builder.and(asset.type.eq(assetType));
        //            } catch (NumberFormatException ignored) {
        //            }
        //        }

        // 카테고리 이름
        if (condition.getCategoryName() != null) {
            builder.and(category.name.eq(condition.getCategoryName()));
        }

        // 자원 상태(int)
        //        if (condition.getAssetStatus() != null) {
        //            try {
        //                builder.and(asset.status.eq(assetStatus));
        //            } catch (NumberFormatException ignored) {
        //            }
        //        }

        BooleanBuilder closureOn = new BooleanBuilder();
        closureOn.and(closure.assetClosureId.descendantId.eq(asset.id));
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

        // 조회
        return query.select(Projections.constructor(
                        RawAppliedReservationResponseDto.class,
                        asset.id,
                        asset.name,
                        reservation.id,
                        applicant.userName,
                        respondent.userName,
                        reservation.status,
                        reservation.isApproved,
                        reservation.reason))
                .from(reservation)
                .join(reservation.asset, asset)
                .leftJoin(asset.category, category)
                .leftJoin(closure)
                .on(closure.assetClosureId.descendantId.eq(asset.id))
                .leftJoin(reservation.applicant, applicant)
                .leftJoin(reservation.respondent, respondent)
                .where(builder)
                .fetch();

    }

}
