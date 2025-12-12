package com.beyond.qiin.domain.inventory.service.query;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.inventory.dto.category.response.DropdownCategoryResponseDto;
import com.beyond.qiin.domain.inventory.dto.category.response.ManageCategoryResponseDto;
import java.util.List;

public interface CategoryQueryService {

    // 드롭 다운용 목록 조회
    List<DropdownCategoryResponseDto> getDropdownList();

    // 관리자용 목록 조회
    PageResponseDto<ManageCategoryResponseDto> getManageList(final int page, final int size);

    // id로 카테고리 가져오기
    //    Category getCategoryById(final Long categoryId);

    // 챗봇이 사용함
    // 카테고리 전체 목록 가져오기
    List<DropdownCategoryResponseDto> findAllCategories();
}
