package com.beyond.qiin.domain.inventory.repository.querydsl;

import com.beyond.qiin.domain.inventory.dto.category.response.CategoryDropdownResponseDto;
import com.beyond.qiin.domain.inventory.dto.category.response.CategoryManageResponseDto;

import java.util.List;

public interface CategoryQueryAdapter {

    List<CategoryDropdownResponseDto> findAllForDropdown();

    List<CategoryManageResponseDto> findAllForManage();
}
