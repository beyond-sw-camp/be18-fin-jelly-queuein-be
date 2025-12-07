//package com.beyond.qiin.domain.booking.repository.querydsl;
//
//import com.beyond.qiin.domain.booking.dto.reservation.request.search_condition.ReservableAssetSearchCondition;
//import com.beyond.qiin.domain.booking.dto.reservation.response.raw.RawReservableAssetResponseDto;
//import com.beyond.qiin.domain.inventory.entity.QAsset;
//import com.beyond.qiin.domain.inventory.entity.QAssetClosure;
//import com.beyond.qiin.domain.inventory.entity.QCategory;
//import com.beyond.qiin.domain.inventory.enums.AssetStatus;
//import com.beyond.qiin.domain.inventory.enums.AssetType;
//import com.querydsl.core.BooleanBuilder;
//import com.querydsl.core.types.Projections;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import java.util.List;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Repository;
//
//@Repository
//@RequiredArgsConstructor
//public class ReservableAssetsQueryRepositoryImpl implements ReservableAssetsQueryRepository {
//
//    private final JPAQueryFactory query;
//
//    private static final QAsset asset = QAsset.asset;
//    private static final QAssetClosure closure = QAssetClosure.assetClosure;
//    private static final QCategory category = QCategory.category;
//
//    @Override
//    public List<RawReservableAssetResponseDto> search(ReservableAssetSearchCondition condition) {
//        BooleanBuilder builder = new BooleanBuilder();
//
//        // 자원 관련
//        // 이름 검색
//        if (condition.getAssetName() != null) {
//            builder.and(asset.name.containsIgnoreCase(condition.getAssetName()));
//        }
//
//        // 자원 유형
//        if (condition.getAssetType() != null) {
//            String raw = condition.getAssetType().trim();
//
//            try {
//                AssetType statusEnum = AssetType.valueOf(raw.toUpperCase());
//
//                builder.and(asset.type.eq(statusEnum.getCode()));
//
//            } catch (IllegalArgumentException ignored) {
//            }
//        }
//
//        // 자원 상태 (assetStatus) 필터링
//        if (condition.getAssetStatus() != null) {
//            String raw = condition.getAssetStatus().trim();
//
//            try {
//                AssetStatus statusEnum = AssetStatus.valueOf(raw.toUpperCase());
//
//                builder.and(asset.status.eq(statusEnum.getCode()));
//
//            } catch (IllegalArgumentException ignored) {
//            }
//        }
//
//        // 카테고리
//        if (condition.getCategoryName() != null) {
//            builder.and(category.name.eq(condition.getCategoryName()));
//        }
//
//        if (condition.getAssetStatus() != null) {
//            try {
//                builder.and(asset.status.eq(Integer.parseInt(condition.getAssetStatus())));
//            } catch (NumberFormatException ignored) {
//            }
//        }
//        BooleanBuilder closureOn = new BooleanBuilder();
//        closureOn.and(closure.assetClosureId.descendantId.eq(asset.id));
//
//        // 0계층 / 1계층
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
//        return query.select(Projections.constructor(
//                        RawReservableAssetResponseDto.class,
//                        asset.id,
//                        asset.name,
//                        asset.type,
//                        category.name,
//                        asset.needsApproval))
//                .from(asset)
//                .leftJoin(category)
//                .on(category.id.eq(asset.category.id))
//                .leftJoin(closure)
//                .on(closure.assetClosureId.descendantId.eq(asset.id))
//                .where(builder)
//                .distinct()
//                .orderBy(asset.id.desc())
//                .fetch();
//    }
//}
