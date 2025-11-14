package com.beyond.qiin.domain.inventory.repository.querydsl;

import com.beyond.qiin.domain.inventory.dto.category.response.DropdownCategoryResponseDto;
import com.beyond.qiin.domain.inventory.dto.category.response.ManageCategoryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryQueryAdapter {

    List<DropdownCategoryResponseDto> findAllForDropdown();

    Page<ManageCategoryResponseDto> findAllForManage(Pageable pageable);
}
