package com.beyond.qiin.domain.inventory.repository.querydsl;

import com.beyond.qiin.domain.inventory.dto.category.response.CategoryDropdownResponseDto;
import com.beyond.qiin.domain.inventory.dto.category.response.CategoryManageResponseDto;
import com.beyond.qiin.domain.inventory.entity.QAsset;
import com.beyond.qiin.domain.inventory.entity.QCategory;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class CategoryQueryAdapterImpl implements CategoryQueryAdapter {

    private final JPAQueryFactory jpaQueryFactory;

    private final QCategory category = QCategory.category;
    private final QAsset asset = QAsset.asset;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDropdownResponseDto> findAllForDropdown() {
        return jpaQueryFactory
                .select(Projections.constructor(CategoryDropdownResponseDto.class, category.id, category.name))
                .from(category)
                .orderBy(category.name.asc())
                .fetch();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryManageResponseDto> findAllForManage() {
        return jpaQueryFactory
                .select(Projections.constructor(
                        CategoryManageResponseDto.class,
                        category.id,
                        category.name,
                        category.description,
                        JPAExpressions.select(asset.count()).from(asset).where(asset.category.id.eq(category.id)),
                        category.createdAt,
                        category.createdBy))
                .from(category)
                .orderBy(category.id.asc())
                .fetch();
    }
}
