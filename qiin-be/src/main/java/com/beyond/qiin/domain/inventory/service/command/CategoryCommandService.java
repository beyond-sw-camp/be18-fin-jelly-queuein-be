package com.beyond.qiin.domain.inventory.service.command;

import com.beyond.qiin.domain.inventory.dto.category.request.CreateCategoryRequestDto;
import com.beyond.qiin.domain.inventory.dto.category.request.UpdateCategoryRequestDto;
import com.beyond.qiin.domain.inventory.dto.category.response.CreateCategoryResponseDto;

public interface CategoryCommandService {

    //create
    CreateCategoryResponseDto createCategory(CreateCategoryRequestDto requestDto);

    //update
    void updateCategory(UpdateCategoryRequestDto requestDto);

    //delete
    void deleteCategory(String categoryId);

}
