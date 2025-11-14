package com.beyond.qiin.domain.inventory.service.query;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.inventory.dto.category.response.DropdownCategoryResponseDto;
import com.beyond.qiin.domain.inventory.dto.category.response.ManageCategoryResponseDto;
import java.util.List;

public interface CategoryQueryService {

    // 드롭 다운용 목록 조회
    List<DropdownCategoryResponseDto> getDropdownList();

    // 관리자용 목록 조회
    PageResponseDto<ManageCategoryResponseDto> getManageList(int page, int size);
}
