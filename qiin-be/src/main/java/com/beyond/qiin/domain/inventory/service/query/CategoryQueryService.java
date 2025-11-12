package com.beyond.qiin.domain.inventory.service.query;

import com.beyond.qiin.domain.inventory.dto.category.response.CategoryDropdownResponseDto;
import com.beyond.qiin.domain.inventory.dto.category.response.CategoryManageResponseDto;
import java.util.List;

public interface CategoryQueryService {

    // 드롭 다운용 목록 조회
    List<CategoryDropdownResponseDto> getDropdownList();

    // 관리자용 목록 조회
    List<CategoryManageResponseDto> getManageList();
}
