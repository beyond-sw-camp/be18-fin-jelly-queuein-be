//package com.beyond.qiin.domain.booking.reservation.repository.querydsl;
//
//import com.beyond.qiin.domain.booking.dto.reservation.request.search_condition.GetAppliedReservationSearchCondition;
//import com.beyond.qiin.domain.booking.dto.reservation.response.GetAppliedReservationResponseDto;
//import com.beyond.qiin.domain.booking.reservation.entity.QReservation;
//import com.beyond.qiin.domain.iam.entity.QUser;
//import com.beyond.qiin.domain.inventory.entity.QAsset;
//import com.beyond.qiin.domain.inventory.entity.QAssetClosure;
//import com.beyond.qiin.domain.inventory.entity.QCategory;
//import com.querydsl.core.BooleanBuilder;
//import com.querydsl.core.types.Projections;
//import com.querydsl.core.types.dsl.BooleanExpression;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import java.time.Instant;
//import java.time.LocalDate;
//import java.time.ZoneId;
//import java.time.temporal.ChronoUnit;
//import java.util.List;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Repository;
//
//@Repository
//@RequiredArgsConstructor
//public class AppliedReservationsQueryRepositoryImpl implements AppliedReservationsQueryRepository {
//
//    private final JPAQueryFactory query;
//
//    private static final QReservation reservation = QReservation.reservation;
//    private static final QAsset asset = QAsset.asset;
//    private static final QCategory category = QCategory.category;
//    private static final QAssetClosure closure = QAssetClosure.assetClosure;
//    private static final QUser applicant = new QUser("applicant");
//    private static final QUser respondent = new QUser("respondent");
//
//    @Override
//    public Page<GetAppliedReservationResponseDto> search(
//            GetAppliedReservationSearchCondition condition, Pageable pageable) {
//
//        BooleanBuilder builder = new BooleanBuilder();
//        builder.and(reservation.status.eq(0)); // pending인 경우 == 신청된 reservations
//
//        // TODO : 날짜를 아직도 모르겠다 UTC니까 LOCAL DATE로 받는게 나은건가
//        // 날짜 조건
//        if (condition.getDate() != null) {
//            LocalDate targetDate =
//                    condition.getDate().atZone(ZoneId.of("Asia/Seoul")).toLocalDate();
//
//            Instant start = targetDate.atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant();
//
//            Instant end = start.plus(1, ChronoUnit.DAYS);
//            builder.and(reservation.startAt.between(start, end));
//        }
//
//        // 신청자 이름 검색
//        if (condition.getApplicantName() != null) {
//            builder.and(applicant.userName.containsIgnoreCase(condition.getApplicantName()));
//        }
//
//        // 승인자 이름 검색
//        if (condition.getRespondentName() != null) {
//            builder.and(respondent.userName.containsIgnoreCase(condition.getRespondentName()));
//        }
//
//        // TODO: isReservable = true → 해당 시간대 다른 예약 없음
//        BooleanExpression reservableCondition = null;
//        if (condition.getIsReservable() != null) {
//            boolean isReservable = Boolean.parseBoolean(condition.getIsReservable());
//            reservableCondition = isReservable
//                    ? reservation.status.eq(0) // 단순화: 승인대기 or 예약가능
//                    : reservation.status.ne(0);
//        }
//
//        // 승인 여부
//        if (condition.getIsApproved() != null) {
//            boolean approved = Boolean.parseBoolean(condition.getIsApproved());
//            builder.and(reservation.isApproved.eq(approved));
//        }
//
//        // 자원명
//        if (condition.getAssetName() != null) {
//            builder.and(asset.name.containsIgnoreCase(condition.getAssetName()));
//        }
//
//        // 자원 유형(int)
//        //        if (condition.getAssetType() != null) {
//        //            try {
//        //                builder.and(asset.type.eq(assetType));
//        //            } catch (NumberFormatException ignored) {
//        //            }
//        //        }
//
//        // 카테고리 이름
//        if (condition.getCategoryName() != null) {
//            builder.and(category.name.eq(condition.getCategoryName()));
//        }
//
//        // 자원 상태(int)
//        //        if (condition.getAssetStatus() != null) {
//        //            try {
//        //                builder.and(asset.status.eq(assetStatus));
//        //            } catch (NumberFormatException ignored) {
//        //            }
//        //        }
//
//        // 계층 필터
//        if (condition.getLayerZero() != null) {
//            builder.and(closure.depth
//                    .eq(0)
//                    .and(closure.assetClosureId.descendantId.eq(asset.id))
//                    .and(closure.assetClosureId.ancestorId.eq(Long.parseLong(condition.getLayerZero()))));
//        }
//
//        if (condition.getLayerOne() != null) {
//            builder.and(closure.depth
//                    .eq(1)
//                    .and(closure.assetClosureId.descendantId.eq(asset.id))
//                    .and(closure.assetClosureId.ancestorId.eq(Long.parseLong(condition.getLayerOne()))));
//        }
//
//        // 조회
//        List<GetAppliedReservationResponseDto> content = query.select(Projections.constructor(
//                        GetAppliedReservationResponseDto.class,
//                        reservation.id,
//                        reservation.startAt,
//                        reservation.endAt,
//                        reservation.status,
//                        reservation.isApproved,
//                        reservation.reason,
//                        applicant.userName,
//                        respondent.userName,
//                        asset.id,
//                        asset.name,
//                        category.name,
//                        asset.status,
//                        asset.type))
//                .from(reservation)
//                .join(asset)
//                .on(asset.id.eq(reservation.asset.id))
//                .leftJoin(category)
//                .on(category.id.eq(asset.categoryId))
//                .leftJoin(applicant)
//                .on(applicant.id.eq(reservation.applicant.id))
//                .leftJoin(respondent)
//                .on(respondent.id.eq(reservation.respondent.id))
//                .leftJoin(closure)
//                .on(closure.assetClosureId.descendantId.eq(asset.id))
//                .where(builder)
//                .where(reservableCondition)
//                .orderBy(reservation.startAt.desc())
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//
//        long total = query.select(reservation.count())
//                .from(reservation)
//                .join(asset)
//                .on(asset.id.eq(reservation.asset.id))
//                .leftJoin(category)
//                .on(category.id.eq(asset.categoryId))
//                .leftJoin(applicant)
//                .on(applicant.id.eq(reservation.applicant.id))
//                .leftJoin(respondent)
//                .on(respondent.id.eq(reservation.respondent.id))
//                .leftJoin(closure)
//                .on(closure.assetClosureId.descendantId.eq(asset.id))
//                .where(builder)
//                .where(reservableCondition)
//                .fetchOne();
//
//        return new PageImpl<>(content, pageable, total);
//    }
//}
