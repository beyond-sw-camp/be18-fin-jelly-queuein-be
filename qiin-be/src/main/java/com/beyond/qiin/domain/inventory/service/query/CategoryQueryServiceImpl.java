package com.beyond.qiin.domain.inventory.service.query;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.inventory.dto.category.response.DropdownCategoryResponseDto;
import com.beyond.qiin.domain.inventory.dto.category.response.ManageCategoryResponseDto;
import com.beyond.qiin.domain.inventory.repository.querydsl.CategoryQueryAdapter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryQueryServiceImpl implements CategoryQueryService {

    private final CategoryQueryAdapter categoryQueryAdapter;

    @Override
    @Transactional(readOnly = true)
    public List<DropdownCategoryResponseDto> getDropdownList() {
        return categoryQueryAdapter.findAllForDropdown();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<ManageCategoryResponseDto> getManageList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<ManageCategoryResponseDto> categoryManageResponseDtoPage = categoryQueryAdapter.findAllForManage(pageable);

        return PageResponseDto.from(categoryManageResponseDtoPage);
    }
}
