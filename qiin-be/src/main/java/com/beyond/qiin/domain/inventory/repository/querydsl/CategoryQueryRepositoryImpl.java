package com.beyond.qiin.domain.inventory.repository.querydsl;

import com.beyond.qiin.domain.inventory.dto.category.response.DropdownCategoryResponseDto;
import com.beyond.qiin.domain.inventory.dto.category.response.ManageCategoryResponseDto;
import com.beyond.qiin.domain.inventory.entity.Category;
import com.beyond.qiin.domain.inventory.entity.QAsset;
import com.beyond.qiin.domain.inventory.entity.QCategory;
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
import org.springframework.transaction.annotation.Transactional;
import static com.beyond.qiin.domain.inventory.entity.QCategory.category;
import static com.beyond.qiin.domain.inventory.entity.QAsset.asset;

@Repository
@RequiredArgsConstructor
public class CategoryQueryRepositoryImpl implements CategoryQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    @Transactional(readOnly = true)
    public List<DropdownCategoryResponseDto> findAllForDropdown() {
        return jpaQueryFactory
                .select(Projections.constructor(DropdownCategoryResponseDto.class, category.id, category.name))
                .from(category)
                .orderBy(category.name.asc())
                .fetch();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ManageCategoryResponseDto> findAllForManage(Pageable pageable) {
        List<ManageCategoryResponseDto> content = jpaQueryFactory
                .select(Projections.constructor(
                        ManageCategoryResponseDto.class,
                        category.id,
                        category.name,
                        category.description,
                        JPAExpressions.select(asset.count()).from(asset).where(asset.category.id.eq(category.id)),
                        category.createdAt,
                        category.createdBy))
                .from(category)
                .orderBy(category.id.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount =
                jpaQueryFactory.select(category.count()).from(category).fetchOne();

        return new PageImpl<>(content, pageable, totalCount);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> findNameById(Long categoryId) {

        Category result = jpaQueryFactory
                .selectFrom(category)
                .where(category.id.eq(categoryId))
                .fetchOne();

        return Optional.ofNullable(result).map(Category::getName);
    }
}
