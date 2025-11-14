package com.beyond.qiin.domain.inventory.service.command;

import com.beyond.qiin.domain.inventory.dto.category.request.CreateCategoryRequestDto;
import com.beyond.qiin.domain.inventory.dto.category.request.UpdateCategoryRequestDto;
import com.beyond.qiin.domain.inventory.dto.category.response.CreateCategoryResponseDto;
import com.beyond.qiin.domain.inventory.entity.Category;

public interface CategoryCommandService {

    // create
    CreateCategoryResponseDto createCategory(final CreateCategoryRequestDto requestDto);

    // update
    void updateCategory(final UpdateCategoryRequestDto requestDto, final Long categoryId);

    // delete
    void deleteCategory(final Long categoryId, final Long userId);

    // 카테고리 검증
    void validateCategoryId(final Long categoryId);
}
