package com.beyond.qiin.domain.inventory.repository.querydsl;

import static com.beyond.qiin.domain.inventory.entity.QAsset.asset;
import static com.beyond.qiin.domain.inventory.entity.QAssetClosure.assetClosure;
import static com.beyond.qiin.domain.inventory.entity.QCategory.category;

import com.beyond.qiin.domain.inventory.dto.asset.request.search_condition.AssetSearchCondition;
import com.beyond.qiin.domain.inventory.dto.asset.response.raw.RawAssetDetailResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.raw.RawDescendantAssetResponseDto;
import com.beyond.qiin.domain.inventory.entity.Asset;
import com.beyond.qiin.domain.inventory.entity.AssetClosure;
import com.beyond.qiin.domain.inventory.entity.QAsset;
import com.beyond.qiin.domain.inventory.enums.AssetStatus;
import com.beyond.qiin.domain.inventory.enums.AssetType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AssetQueryRepositoryImpl implements AssetQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Asset> findById(Long assetId) {
        Asset result = jpaQueryFactory
                .select(asset)
                .from(asset)
                .where(asset.id.eq(assetId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<Long> findRootAssetIds() {
        return jpaQueryFactory
                .select(assetClosure.assetClosureId.descendantId)
                .from(assetClosure)
                .where(assetClosure.depth.eq(0))
                .where(assetClosure.assetClosureId.descendantId.notIn(
                        JPAExpressions.select(assetClosure.assetClosureId.descendantId)
                                .from(assetClosure)
                                .where(assetClosure.depth.eq(1))))
                .fetch();
    }

    @Override
    public List<Long> findChildrenIds(Long assetId) {
        return jpaQueryFactory
                .select(assetClosure.assetClosureId.descendantId)
                .from(assetClosure)
                .join(asset)
                .on(asset.id.eq(assetClosure.assetClosureId.descendantId))
                .where(assetClosure.assetClosureId.ancestorId.eq(assetId), assetClosure.depth.eq(1))
                .orderBy(asset.name.asc())
                .fetch();
    }

    @Override
    public List<AssetClosure> findSubtree(Long assetId) {
        return jpaQueryFactory
                .select(assetClosure)
                .from(assetClosure)
                .where(assetClosure.assetClosureId.ancestorId.eq(assetId))
                .orderBy(assetClosure.depth.asc())
                .fetch();
    }

    @Override
    public List<Asset> findByIds(List<Long> ids) {
        return jpaQueryFactory
                .select(asset)
                .from(asset)
                .where(asset.id.in(ids))
                .orderBy(asset.name.asc())
                .fetch();
    }

    public Page<RawDescendantAssetResponseDto> findAllForDescendant(Pageable pageable) {

        List<RawDescendantAssetResponseDto> contnet = jpaQueryFactory
                .select(Projections.constructor(
                        RawDescendantAssetResponseDto.class,
                        asset.id,
                        asset.name,
                        asset.category.name,
                        asset.status,
                        asset.type,
                        asset.needsApproval,
                        asset.status.eq(0),
                        asset.version))
                .from(asset)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(asset.name.asc())
                .fetch();

        long totalCount = jpaQueryFactory.select(asset.count()).from(asset).fetchOne();

        return new PageImpl<>(contnet, pageable, totalCount);
    }

    @Override
    public Optional<RawAssetDetailResponseDto> findByAssetId(Long assetId) {

        RawAssetDetailResponseDto dto = jpaQueryFactory
                .select(Projections.constructor(
                        RawAssetDetailResponseDto.class,
                        asset.id,
                        asset.category.id,
                        asset.category.name,
                        asset.name,
                        asset.description,
                        asset.image,
                        asset.status,
                        asset.type,
                        asset.accessLevel,
                        asset.needsApproval,
                        asset.costPerHour,
                        asset.periodCost,
                        asset.createdAt,
                        asset.createdBy))
                .from(asset)
                .where(asset.id.eq(assetId))
                .fetchOne();

        return Optional.ofNullable(dto);
    }

    @Override
    public String findParentName(Long assetId) {

        QAsset parent = new QAsset("parent");

        return jpaQueryFactory
                .select(parent.name)
                .from(assetClosure)
                .leftJoin(parent)
                .on(parent.id.eq(assetClosure.assetClosureId.ancestorId))
                .where(assetClosure.assetClosureId.descendantId.eq(assetId), assetClosure.depth.eq(1))
                .fetchOne();
    }

    @Override
    public List<Asset> findAll() {
        return jpaQueryFactory.selectFrom(asset).fetch();
    }

    @Override
    public Page<RawDescendantAssetResponseDto> searchDescendants(AssetSearchCondition condition, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        // 🔹 root(0 depth) 조건이 들어오면 join 필요
        boolean needsClosureJoin = false;

        // 🔹 1depth가 있는 경우 → 이것만 적용됨 (가장 좁은 범위)
        if (condition.getOneDepth() != null) {
            needsClosureJoin = true;
            builder.and(assetClosure.assetClosureId.ancestorId.eq(Long.valueOf(condition.getOneDepth())))
                    .and(assetClosure.depth.gt(0)); // 자기 자신 제외
        }
        // 🔹 그 외 root가 있는 경우
        else if (condition.getRoot() != null) {
            needsClosureJoin = true;
            builder.and(assetClosure.assetClosureId.ancestorId.eq(Long.valueOf(condition.getRoot())))
                    .and(assetClosure.depth.gt(0)); // depth > 0 로 자기 자신 제외
        }

        if (condition.getCategoryId() != null) {
            builder.and(asset.category.id.eq(condition.getCategoryId()));
        }

        if (condition.getType() != null) {
            builder.and(asset.type.eq(AssetType.fromName(condition.getType()).getCode()));
        }

        if (condition.getStatus() != null) {
            builder.and(
                    asset.status.eq(AssetStatus.fromName(condition.getStatus()).getCode()));
        }

        if (condition.getKeyword() != null && !condition.getKeyword().isEmpty()) {
            builder.and(asset.name
                    .containsIgnoreCase(condition.getKeyword())
                    .or(asset.description.containsIgnoreCase(condition.getKeyword())));
        }
        // 여기에서 join 조건을 동적으로 적용함
        var query = jpaQueryFactory
                .select(Projections.constructor(
                        RawDescendantAssetResponseDto.class,
                        asset.id,
                        asset.name,
                        category.name,
                        asset.status,
                        asset.type,
                        asset.needsApproval,
                        asset.status.eq(0),
                        asset.version))
                .from(asset)
                .leftJoin(category)
                .on(category.id.eq(asset.category.id));

        if (needsClosureJoin) {
            query.leftJoin(assetClosure).on(assetClosure.assetClosureId.descendantId.eq(asset.id));
        }

        List<RawDescendantAssetResponseDto> content = query.where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // count 쿼리도 동일하게 closure join 반영!
        var countQuery = jpaQueryFactory
                .select(asset.count())
                .from(asset)
                .leftJoin(category)
                .on(category.id.eq(asset.category.id));

        if (needsClosureJoin) {
            countQuery.leftJoin(assetClosure).on(assetClosure.assetClosureId.descendantId.eq(asset.id));
        }

        long total = countQuery.where(builder).fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public List<RawDescendantAssetResponseDto> searchDescendantsAsList(AssetSearchCondition condition) {

        BooleanBuilder builder = new BooleanBuilder();

        boolean needsClosureJoin = false;

        if (condition.getOneDepth() != null) {
            needsClosureJoin = true;
            builder.and(assetClosure.assetClosureId.ancestorId.eq(Long.valueOf(condition.getOneDepth())))
                    .and(assetClosure.depth.gt(0));
        } else if (condition.getRoot() != null) {
            needsClosureJoin = true;
            builder.and(assetClosure.assetClosureId.ancestorId.eq(Long.valueOf(condition.getRoot())))
                    .and(assetClosure.depth.gt(0));
        }

        if (condition.getCategoryId() != null) {
            builder.and(asset.category.id.eq(condition.getCategoryId()));
        }

        if (condition.getType() != null) {
            builder.and(asset.type.eq(AssetType.fromName(condition.getType()).getCode()));
        }

        if (condition.getStatus() != null) {
            builder.and(
                    asset.status.eq(AssetStatus.fromName(condition.getStatus()).getCode()));
        }

        if (condition.getKeyword() != null && !condition.getKeyword().isEmpty()) {
            builder.and(asset.name
                    .containsIgnoreCase(condition.getKeyword())
                    .or(asset.description.containsIgnoreCase(condition.getKeyword())));
        }

        var query = jpaQueryFactory
                .select(Projections.constructor(
                        RawDescendantAssetResponseDto.class,
                        asset.id,
                        asset.name,
                        category.name,
                        asset.status,
                        asset.type,
                        asset.needsApproval,
                        asset.status.eq(0),
                        asset.version))
                .from(asset)
                .leftJoin(category)
                .on(category.id.eq(asset.category.id));

        if (needsClosureJoin) {
            query.leftJoin(assetClosure).on(assetClosure.assetClosureId.descendantId.eq(asset.id));
        }

        return query.where(builder).fetch();
    }
}
